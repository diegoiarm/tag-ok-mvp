package com.tagok.routes_service.service.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.dto.request.PorticoRequest;
import com.tagok.routes_service.dto.response.CalendarioTarifarioResponse;
import com.tagok.routes_service.dto.response.PorticoResponse;
import com.tagok.routes_service.dto.response.PorticoResumenResponse;
import com.tagok.routes_service.dto.response.ReglaTarifariaResponse;

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
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .sentido(request.getSentido())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .build();

        mapReglasFromRequest(request, portico);
        mapCalendarioFromRequest(request, portico);

        return portico;
    }

    private void mapReglasFromRequest(PorticoRequest request, Portico portico) 
    {
        Optional.ofNullable(request.getReglas())
                .ifPresent(reglas -> reglas.stream()
                        .map(reglaTarifariaMapper::fromRequest)
                        .forEach(portico::addRegla));
    }

    private void mapCalendarioFromRequest(PorticoRequest request, Portico portico) 
    {
        Optional.ofNullable(request.getCalendario())
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

    public PorticoResumenResponse toResumenResponse(Portico portico)
    {
        return PorticoResumenResponse.builder()
                .id(portico.getId())
                .codigo(portico.getCodigo())
                .nombre(portico.getNombre())
                .sentido(portico.getSentido())
                .latitud(portico.getLatitud())
                .longitud(portico.getLongitud())
                .build();
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