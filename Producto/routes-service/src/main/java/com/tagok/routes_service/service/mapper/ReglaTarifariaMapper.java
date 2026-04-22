package com.tagok.routes_service.service.mapper;

import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.ReglaTarifaria;
import com.tagok.routes_service.dto.request.ReglaTarifariaRequest;
import com.tagok.routes_service.dto.response.ReglaTarifariaResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReglaTarifariaMapper implements IEntityMapper<ReglaTarifariaResponse, ReglaTarifariaRequest, ReglaTarifaria> 
{
    private final ValorTarifaMapper valorTarifaMapper;

    @Override
    public ReglaTarifaria fromRequest(ReglaTarifariaRequest request) 
    {
        ReglaTarifaria regla = ReglaTarifaria.builder()
                .aplicaA(request.getAplicaA())
                .build();

        Optional.ofNullable(request.getValores())
                .ifPresent(valores -> valores.stream()
                        .map(valorTarifaMapper::fromRequest)
                        .forEach(regla::addValor));

        return regla;
    }

    @Override
    public ReglaTarifariaResponse toResponse(ReglaTarifaria entity) 
    {
        return ReglaTarifariaResponse.builder()
                .aplicaA(entity.getAplicaA())
                .valores(
                        Optional.ofNullable(entity.getValores())
                                .map(valores -> valores.stream()
                                        .map(valorTarifaMapper::toResponse)
                                        .toList())
                                .orElseGet(Collections::emptyList))
                .build();
    }
}