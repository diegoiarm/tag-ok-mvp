package com.tagok.routes_service.service.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.dto.request.autopista.AutopistaRequest;
import com.tagok.routes_service.dto.request.autopista.AutopistaUpdateRequest;
import com.tagok.routes_service.dto.response.autopista.AutopistaResponse;
import com.tagok.routes_service.exception.DuplicateResourceException;
import com.tagok.routes_service.exception.ResourceNotFoundException;
import com.tagok.routes_service.repository.AutopistaRepository;
import com.tagok.routes_service.service.mapper.AutopistaMapper;
import com.tagok.routes_service.service.mapper.PorticoMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutopistaService 
{
    private final AutopistaRepository autopistaRepository;
    private final AutopistaMapper autopistaMapper;
    private final PorticoMapper porticoMapper;

    /**
     * Crea una concesionaria nueva de forma estricta: rechaza nombres o códigos
     * ya existentes. Usado por el alta manual desde el panel de administración.
     */
    @Transactional
    public AutopistaResponse create(AutopistaRequest request)
    {
        if (autopistaRepository.existsByNombre(request.autopista()))
            throw new DuplicateResourceException(
                "Ya existe una concesionaria con el nombre: " + request.autopista());

        if (autopistaRepository.existsByCodigo(request.codigo()))
            throw new DuplicateResourceException(
                "Ya existe una concesionaria con el código: " + request.codigo());

        Autopista autopista = autopistaMapper.fromRequest(request);
        return autopistaMapper.toResponse(autopistaRepository.save(autopista));
    }

    /**
     * Actualiza los metadatos editables (nombre y código) de una concesionaria.
     * No modifica el tipo de cobro ni los pórticos/tramos.
     */
    @Transactional
    public AutopistaResponse update(Long id, AutopistaUpdateRequest request)
    {
        Autopista autopista = autopistaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Concesionaria no encontrada: " + id));

        if (autopistaRepository.existsByNombreAndIdNot(request.nombre(), id))
            throw new DuplicateResourceException(
                "Ya existe una concesionaria con el nombre: " + request.nombre());

        if (autopistaRepository.existsByCodigoAndIdNot(request.codigo(), id))
            throw new DuplicateResourceException(
                "Ya existe una concesionaria con el código: " + request.codigo());

        autopista.setNombre(request.nombre());
        autopista.setCodigo(request.codigo());

        return autopistaMapper.toResponse(autopistaRepository.save(autopista));
    }

    /**
     * Crea o actualiza (upsert por nombre) una concesionaria con sus pórticos.
     * Usado por la importación masiva de archivos JSON.
     */
    @Transactional
    public AutopistaResponse saveAutopistaWithPorticos(AutopistaRequest request)
    {
        Autopista autopista = autopistaRepository.findByNombre(request.autopista())
                .orElseGet(() -> autopistaMapper.fromRequest(request));

        request.porticos()
            .forEach(porticoRequest ->
            {
                Portico portico = porticoMapper.fromRequest(porticoRequest);
                autopista.addPortico(portico);
            });

        Autopista autopistaGuardada = autopistaRepository.save(autopista);
        return autopistaMapper.toResponse(autopistaGuardada);
    }

    public List<AutopistaResponse> findAll()
    {
        var autopistas = autopistaRepository.findAll();

        return autopistas.stream()
            .map(autopista -> autopistaMapper.toResponse(autopista))
            .toList();
    }

    public void deleteById(Long id)
    {
        autopistaRepository.deleteById(id);
    }

    public void deleteAll()
    {
        autopistaRepository.deleteAll();
    }
}
