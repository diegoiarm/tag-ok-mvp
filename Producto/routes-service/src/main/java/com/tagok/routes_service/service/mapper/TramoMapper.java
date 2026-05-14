package com.tagok.routes_service.service.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tramo.Tramo;
import com.tagok.routes_service.dto.request.tramo.TramoRequest;
import com.tagok.routes_service.dto.response.CalendarioTarifarioResponse;
import com.tagok.routes_service.dto.response.ReglaTarifariaResponse;
import com.tagok.routes_service.dto.response.TramoResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TramoMapper
{
    private final PorticoMapper porticoMapper;
    private final ReglaTarifariaMapper reglaTarifariaMapper;
    private final CalendarioTarifarioMapper calendarioTarifarioMapper;

    public Tramo fromRequest(TramoRequest request, Portico entrada, Portico salida)
    {
        Tramo tramo = Tramo.builder()
            .entrada(entrada)
            .salida(salida)
            .distanciaKm(request.distancia())
            .area(request.area())
            .sentido(request.sentido())
            .build();

        mapReglasFromRequest(request, tramo);
        mapCalendarioFromRequest(request, tramo);

        return tramo;
    }

    private void mapReglasFromRequest(
        TramoRequest request,
        Tramo tramo)
    {
        Optional.ofNullable(request.reglas())
            .ifPresent(reglas -> reglas.stream()
                .map(reglaTarifariaMapper::fromRequest)
                .forEach(tramo::addRegla));
    }

    private void mapCalendarioFromRequest(
        TramoRequest request,
        Tramo tramo)
    {
        Optional.ofNullable(request.calendario())
            .map(calendarioTarifarioMapper::fromRequest)
            .ifPresent(tramo::setCalendario);
    }

    public TramoResponse toResponse(Tramo tramo)
    {
        String nombreAutopista = null;

        if (tramo.getAutopista() != null)
            nombreAutopista = tramo.getAutopista().getNombre();

        return TramoResponse.builder()
            .id(tramo.getId())
            .entrada(porticoMapper.toResumenResponse(tramo.getEntrada()))
            .salida(porticoMapper.toResumenResponse(tramo.getSalida()))
            .distanciaKm(tramo.getDistanciaKm())
            .autopista(nombreAutopista)
            .reglas(mapReglasToResponse(tramo))
            .calendario(mapCalendarioToResponse(tramo))
            .build();
    }

    private List<ReglaTarifariaResponse> mapReglasToResponse(Tramo tramo)
    {
        return Optional.ofNullable(tramo.getReglas())
            .map(reglas -> reglas.stream()
                .map(reglaTarifariaMapper::toResponse)
                .toList())
            .orElseGet(Collections::emptyList);
    }

    private CalendarioTarifarioResponse mapCalendarioToResponse(Tramo tramo)
    {
        return Optional.ofNullable(tramo.getCalendario())
            .map(calendarioTarifarioMapper::toResponse)
            .orElse(null);
    }
}