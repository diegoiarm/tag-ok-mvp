package com.tagok.routes_service.service.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.autopista.TipoCobro;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tarifa.ReglaTarifaria;
import com.tagok.routes_service.dto.request.portico.PorticoRequest;
import com.tagok.routes_service.dto.request.tarifa.TarifaConfigRequest;
import com.tagok.routes_service.dto.response.portico.CalendarioTarifarioResponse;
import com.tagok.routes_service.dto.response.portico.PorticoAdminResponse;
import com.tagok.routes_service.dto.response.portico.PorticoResponse;
import com.tagok.routes_service.dto.response.portico.PorticoResumenResponse;
import com.tagok.routes_service.dto.response.portico.PorticoTramoResponse;
import com.tagok.routes_service.dto.response.portico.ReglaTarifariaResponse;
import com.tagok.routes_service.dto.response.portico.TramoResponse;
import com.tagok.routes_service.dto.response.tarifa.TarifaConfigResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PorticoMapper 
{
    private final ReglaTarifariaMapper reglaTarifariaMapper;
    private final CalendarioTarifarioMapper calendarioTarifarioMapper;

    public Portico fromRequest(PorticoRequest request) 
    {
        Portico portico = Portico.builder()
                .codigo(request.codigo())
                .nombre(request.nombre())
                .sentido(request.sentido())
                .latitud(request.latitud())
                .longitud(request.longitud())
                .build();

        mapReglasFromRequest(request, portico);
        mapCalendarioFromRequest(request, portico);

        return portico;
    }

    private void mapReglasFromRequest(PorticoRequest request, Portico portico) 
    {
        Optional.ofNullable(request.reglas())
                .ifPresent(reglas -> reglas.stream()
                        .map(reglaTarifariaMapper::fromRequest)
                        .forEach(portico::addRegla));
    }

    private void mapCalendarioFromRequest(PorticoRequest request, Portico portico) 
    {
        Optional.ofNullable(request.calendario())
                .map(calendarioTarifarioMapper::fromRequest)
                .ifPresent(portico::setCalendario);
    }

    public PorticoResponse toResponse(Portico portico) 
    {
        String nombreAutopista = null;
        if (portico.getAutopista() != null)
                nombreAutopista = portico.getAutopista().getNombre();

        return PorticoResponse.builder()
                .id(portico.getId())
                .codigo(portico.getCodigo())
                .nombre(portico.getNombre())
                .sentido(portico.getSentido())
                .latitud(portico.getLatitud())
                .longitud(portico.getLongitud())
                .autopista(nombreAutopista)
                .reglas(mapReglasToResponse(portico))
                .calendario(mapCalendarioToResponse(portico))
                .build();
    }

    public PorticoTramoResponse toTramoResponse(Portico portico)
    {
        return PorticoTramoResponse.builder()
            .id(portico.getId())
            .codigo(portico.getCodigo())
            .nombre(portico.getNombre())
            .autopista(portico.getAutopista().getNombre())
            .tramos(getTramos(portico, portico.getAutopista()))
            .build();
    }

    private List<TramoResponse> getTramos(Portico portico, Autopista autopista)
    {
        return autopista.getTramos().stream()
            .filter(t -> t.getEntrada().equals(portico) || t.getSalida().equals(portico))
            .map(t -> new TramoResponse(
                t.getEntrada().getCodigo(),
                t.getEntrada().getNombre(),
                t.getSalida().getCodigo(),
                t.getSalida().getNombre(),
                getReglas(t.getReglas()),
                calendarioTarifarioMapper.toResponse(t.getCalendario())
            ))
            .toList();
    }

    private List<ReglaTarifariaResponse> getReglas(List<ReglaTarifaria> reglas)
    {
        return reglas.stream()
            .map(reglaTarifariaMapper::toResponse)
            .toList();
    }

    public PorticoAdminResponse toAdminResponse(Portico portico)
    {
        Autopista autopista = portico.getAutopista();

        return PorticoAdminResponse.builder()
                .id(portico.getId())
                .codigo(portico.getCodigo())
                .nombre(portico.getNombre())
                .sentido(portico.getSentido())
                .latitud(portico.getLatitud())
                .longitud(portico.getLongitud())
                // Los pórticos creados antes de existir esta columna quedan como vigentes.
                .activo(portico.getActivo() == null || portico.getActivo())
                .autopistaId(autopista != null ? autopista.getId() : null)
                .autopistaNombre(autopista != null ? autopista.getNombre() : null)
                .autopistaCodigo(autopista != null ? autopista.getCodigo() : null)
                .fechaCreacion(portico.getFechaCreacion())
                .fechaActualizacion(portico.getFechaActualizacion())
                .tieneTarifa(tieneTarifa(portico))
                .build();
    }

    /** Configuración tarifaria (reglas + calendario) de un pórtico, para edición admin (CU19). */
    public TarifaConfigResponse toTarifaConfig(Portico portico)
    {
        return TarifaConfigResponse.builder()
                .reglas(mapReglasToResponse(portico))
                .calendario(mapCalendarioToResponse(portico))
                .build();
    }

    /**
     * Reemplaza por completo la configuración tarifaria del pórtico con la del request.
     * Gracias a {@code orphanRemoval}, limpiar la colección y reasignar el calendario
     * elimina los registros antiguos al persistir.
     */
    public void aplicarTarifaConfig(Portico portico, TarifaConfigRequest request)
    {
        portico.getReglas().clear();
        Optional.ofNullable(request.reglas())
                .ifPresent(reglas -> reglas.stream()
                        .map(reglaTarifariaMapper::fromRequest)
                        .forEach(portico::addRegla));

        portico.setCalendario(
                Optional.ofNullable(request.calendario())
                        .map(calendarioTarifarioMapper::fromRequest)
                        .orElse(null));
    }

    public PorticoResumenResponse toResumenResponse(Portico portico)
    {
        return PorticoResumenResponse.builder()
                .id(portico.getId())
                .nombre(portico.getNombre())
                .latitud(portico.getLatitud())
                .longitud(portico.getLongitud())
                .tieneTarifa(tieneTarifa(portico))
                .build();
    }

    /** Un pórtico participa en el cobro si su autopista es por tramo o tiene calendario + reglas. */
    private boolean tieneTarifa(Portico portico)
    {
        Autopista autopista = portico.getAutopista();
        if (autopista != null && autopista.getTipoCobro() == TipoCobro.TRAMO)
                return true;

        return portico.getCalendario() != null
                && portico.getReglas() != null
                && !portico.getReglas().isEmpty();
    }

    private List<ReglaTarifariaResponse> mapReglasToResponse(Portico portico) 
    {
        return Optional.ofNullable(portico.getReglas())
                .map(reglas -> reglas.stream()
                        .map(reglaTarifariaMapper::toResponse)
                        .toList())
                .orElseGet(Collections::emptyList);
    }

    private CalendarioTarifarioResponse mapCalendarioToResponse(Portico portico) 
    {
        return Optional.ofNullable(portico.getCalendario())
                .map(calendarioTarifarioMapper::toResponse)
                .orElse(null);
    }
}