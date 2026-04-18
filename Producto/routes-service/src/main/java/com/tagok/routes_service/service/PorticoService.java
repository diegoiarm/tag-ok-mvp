package com.tagok.routes_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.dto.response.PorticoResponse;
import com.tagok.routes_service.repository.PorticoRepository;
import com.tagok.routes_service.service.mapper.PorticoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PorticoService 
{
    private final PorticoRepository porticoRepository;
    private final PorticoMapper porticoMapper;

    public List<PorticoResponse> findAll()
    {
        var porticos = porticoRepository.findAll();

        return porticos.stream()
            .map(porticoMapper::toResponse)
            .toList();
    }
}
