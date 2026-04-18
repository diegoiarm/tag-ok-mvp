package com.tagok.routes_service.service;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.Autopista;
import com.tagok.routes_service.domain.dto.request.AutopistaRequest;
import com.tagok.routes_service.domain.dto.response.AutopistaResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutopistaMapper 
{
    private final PorticoMapper porticoMapper;

    public Autopista fromRequest(AutopistaRequest request)
    {
        return Autopista.builder()
            .nombre(request.getAutopista())
            .build();
    }

    public AutopistaResponse toResponse(Autopista autopista)
    {
        return AutopistaResponse.builder()
            .id(autopista.getId())
            .nombre(autopista.getNombre())
            .porticos(
                autopista.getPorticos().stream()
                    .map(porticoMapper::toResponse)
                    .toList()
            )
            .build();
    }
}
