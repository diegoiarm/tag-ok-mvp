package com.tagok.routes_service.service.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.calendario.ReglaTemporal;
import com.tagok.routes_service.dto.request.portico.ReglaTemporalRequest;
import com.tagok.routes_service.dto.response.ReglaTemporalResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReglaTemporalMapper implements IEntityMapper<ReglaTemporalResponse, ReglaTemporalRequest, ReglaTemporal>
{
    private final RangoHorarioMapper rangoHorarioMapper;

    public ReglaTemporal fromRequest(ReglaTemporalRequest request)
    {
        ReglaTemporal regla = new ReglaTemporal();
        regla.setTipoTarifa(request.tipoTarifa());
        regla.setTipoDia(request.tipoDia());

        if (request.tramos() != null)
            request.tramos()
                    .stream()
                    .map(rangoHorarioMapper::fromRequest)
                    .forEach(regla::addTramo);

        return regla;
    }

    public ReglaTemporalResponse toResponse(ReglaTemporal entity)
    {
        return ReglaTemporalResponse.builder()
                .tipoTarifa(entity.getTipoTarifa())
                .tipoDia(entity.getTipoDia())
                .tramos(
                        entity.getTramos() != null
                                ? entity.getTramos()
                                        .stream()
                                        .map(rangoHorarioMapper::toResponse)
                                        .collect(Collectors.toList())
                                : java.util.Collections.emptyList()
                )
                .build();
    }
}
