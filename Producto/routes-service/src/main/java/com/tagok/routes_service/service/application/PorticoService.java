package com.tagok.routes_service.service.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.autopista.TipoCobro;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.dto.request.portico.PorticoBulkItem;
import com.tagok.routes_service.dto.request.portico.PorticoCreateRequest;
import com.tagok.routes_service.dto.request.portico.PorticoUpdateRequest;
import com.tagok.routes_service.dto.response.portico.BulkResultResponse;
import com.tagok.routes_service.dto.response.portico.PorticoAdminResponse;
import com.tagok.routes_service.dto.response.portico.PorticoResumenResponse;
import com.tagok.routes_service.dto.response.portico.TollResponse;
import com.tagok.routes_service.exception.DuplicateResourceException;
import com.tagok.routes_service.exception.ResourceNotFoundException;
import com.tagok.routes_service.repository.AutopistaRepository;
import com.tagok.routes_service.repository.PorticoRepository;
import com.tagok.routes_service.service.mapper.PorticoMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PorticoService
{
    private final PorticoRepository porticoRepository;
    private final AutopistaRepository autopistaRepository;
    private final PorticoMapper porticoMapper;

    public List<PorticoResumenResponse> findAll()
    {
        return porticoRepository.findAll().stream()
            .filter(p -> p.getAutopista().getTipoCobro() == TipoCobro.TRAMO || (p.getCalendario() != null && !p.getReglas().isEmpty()))
            .map(porticoMapper::toResumenResponse)
            .toList();
    }

    public TollResponse findById(long id)
    {
        Portico portico = porticoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Portico no encontrado"));

        if (esTramoEspecialComoPortico(portico))
            return porticoMapper.toResponse(portico);

        return portico.getAutopista().getTipoCobro() == TipoCobro.PORTICO ? porticoMapper.toResponse(portico) : porticoMapper.toTramoResponse(portico);
    }

    // Sirve para identificar si una autopista contramos existe un portico solo
    // para que el sistema identificara eso mano, si aca se da altiro literal hay 1
    // que sentido tiene
    private boolean esTramoEspecialComoPortico(Portico portico)
    {
        return portico.getAutopista().getCodigo().equals("AVO1")
            && portico.getCodigo().equals("P110");
    }

    /* ===================== Gestión administrativa (CU20) ===================== */

    /** Lista todos los pórticos para la gestión administrativa, incluidos los desactivados. */
    public List<PorticoAdminResponse> findAllForAdmin()
    {
        return porticoRepository.findAll().stream()
            .sorted(Comparator.comparing(Portico::getId))
            .map(porticoMapper::toAdminResponse)
            .toList();
    }

    /** Alta manual de un pórtico asociado a una autopista existente. */
    @Transactional
    public PorticoAdminResponse create(PorticoCreateRequest request)
    {
        if (porticoRepository.existsByCodigo(request.codigo()))
            throw new DuplicateResourceException(
                "Ya existe un pórtico con el código: " + request.codigo());

        Autopista autopista = autopistaRepository.findById(request.autopistaId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Autopista no encontrada: " + request.autopistaId()));

        Portico portico = Portico.builder()
            .codigo(request.codigo())
            .nombre(request.nombre())
            .sentido(request.sentido())
            .latitud(request.latitud())
            .longitud(request.longitud())
            .activo(true)
            .autopista(autopista)
            .build();

        return porticoMapper.toAdminResponse(porticoRepository.save(portico));
    }

    /** Edita los atributos de un pórtico, permitiendo reasignar autopista y estado. */
    @Transactional
    public PorticoAdminResponse update(Long id, PorticoUpdateRequest request)
    {
        Portico portico = porticoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Pórtico no encontrado: " + id));

        if (porticoRepository.existsByCodigoAndIdNot(request.codigo(), id))
            throw new DuplicateResourceException(
                "Ya existe un pórtico con el código: " + request.codigo());

        Autopista autopista = autopistaRepository.findById(request.autopistaId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Autopista no encontrada: " + request.autopistaId()));

        portico.setCodigo(request.codigo());
        portico.setNombre(request.nombre());
        portico.setSentido(request.sentido());
        portico.setLatitud(request.latitud());
        portico.setLongitud(request.longitud());
        portico.setAutopista(autopista);
        if (request.activo() != null)
            portico.setActivo(request.activo());

        return porticoMapper.toAdminResponse(porticoRepository.save(portico));
    }

    /** Cambia el estado vigente/desactivado de un pórtico (soft-delete histórico). */
    @Transactional
    public PorticoAdminResponse cambiarEstado(Long id, boolean activo)
    {
        Portico portico = porticoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Pórtico no encontrado: " + id));

        portico.setActivo(activo);
        return porticoMapper.toAdminResponse(porticoRepository.save(portico));
    }

    @Transactional
    public void deleteById(Long id)
    {
        if (!porticoRepository.existsById(id))
            throw new ResourceNotFoundException("Pórtico no encontrado: " + id);

        porticoRepository.deleteById(id);
    }

    /**
     * Carga masiva de pórticos. Cada fila referencia su autopista por código.
     * Las filas inválidas se omiten y se reportan; las válidas se crean.
     */
    @Transactional
    public BulkResultResponse crearMasivo(List<PorticoBulkItem> items)
    {
        if (items == null || items.isEmpty())
            return BulkResultResponse.builder().creados(0).fallidos(0).errores(List.of()).build();

        List<String> errores = new ArrayList<>();
        int creados = 0;
        int fila = 0;

        for (PorticoBulkItem item : items)
        {
            fila++;
            String error = validarYCrear(item, fila);
            if (error != null)
                errores.add(error);
            else
                creados++;
        }

        return BulkResultResponse.builder()
            .creados(creados)
            .fallidos(errores.size())
            .errores(errores)
            .build();
    }

    private String validarYCrear(PorticoBulkItem item, int fila)
    {
        if (item == null || isBlank(item.codigo()) || isBlank(item.nombre()))
            return "Fila " + fila + ": código y nombre son obligatorios.";

        if (isBlank(item.autopistaCodigo()))
            return "Fila " + fila + " (" + item.codigo() + "): falta el código de autopista.";

        if (item.latitud() == null || item.longitud() == null)
            return "Fila " + fila + " (" + item.codigo() + "): latitud y longitud son obligatorias.";

        if (porticoRepository.existsByCodigo(item.codigo()))
            return "Fila " + fila + " (" + item.codigo() + "): ya existe un pórtico con ese código.";

        Autopista autopista = autopistaRepository.findByCodigo(item.autopistaCodigo()).orElse(null);
        if (autopista == null)
            return "Fila " + fila + " (" + item.codigo() + "): autopista no encontrada con código "
                + item.autopistaCodigo() + ".";

        Portico portico = Portico.builder()
            .codigo(item.codigo())
            .nombre(item.nombre())
            .sentido(item.sentido())
            .latitud(item.latitud())
            .longitud(item.longitud())
            .activo(true)
            .autopista(autopista)
            .build();

        porticoRepository.save(portico);
        return null;
    }

    private boolean isBlank(String value)
    {
        return value == null || value.isBlank();
    }
}
