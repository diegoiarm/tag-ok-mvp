package com.tagok.routes_service.service.mapper;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.dto.request.autopista.AutopistaRequest;
import com.tagok.routes_service.dto.response.AutopistaResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutopistaMapper implements IEntityMapper<AutopistaResponse, AutopistaRequest, Autopista>
{
    private final PorticoMapper porticoMapper;

    @Override
    public Autopista fromRequest(AutopistaRequest request)
    {
        return Autopista.builder()
            .nombre(request.getAutopista())
            .porticos(request.getPorticos().stream()
                .map(porticoMapper::fromRequest)
                .toList())
            .build();
    }

    @Override
    public AutopistaResponse toResponse(Autopista autopista)
    {
        return AutopistaResponse.builder()
            .id(autopista.getId())
            .nombre(autopista.getNombre())
            .porticos(autopista.getPorticos().stream()
                .map(porticoMapper::toResponse)
                .toList())
            .build();
    }
}
