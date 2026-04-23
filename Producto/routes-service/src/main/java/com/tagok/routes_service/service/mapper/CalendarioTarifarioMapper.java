package com.tagok.routes_service.service.mapper;

import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.calendario.CalendarioTarifario;
import com.tagok.routes_service.dto.request.portico.CalendarioTarifarioRequest;
import com.tagok.routes_service.dto.response.CalendarioTarifarioResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CalendarioTarifarioMapper implements IEntityMapper<CalendarioTarifarioResponse, CalendarioTarifarioRequest, CalendarioTarifario> 
{
    private final ReglaTemporalMapper reglaTemporalMapper;

    @Override
    public CalendarioTarifario fromRequest(CalendarioTarifarioRequest request) 
    {
        CalendarioTarifario calendario = new CalendarioTarifario();
        
        Optional.ofNullable(request.reglas())
                .ifPresent(reglas -> reglas.stream()
                        .map(reglaTemporalMapper::fromRequest)
                        .forEach(calendario::addRegla));
        
        return calendario;
    }

    @Override
    public CalendarioTarifarioResponse toResponse(CalendarioTarifario entity) 
    {
        return CalendarioTarifarioResponse.builder()
                .reglas(
                        Optional.ofNullable(entity.getReglas())
                                .map(reglas -> reglas.stream()
                                        .map(reglaTemporalMapper::toResponse)
                                        .toList())
                                .orElseGet(Collections::emptyList))
                .build();
    }
}