package com.tagok.routes_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.dto.response.PorticoResponse;
import com.tagok.routes_service.dto.response.PorticoResumenResponse;
import com.tagok.routes_service.repository.PorticoRepository;
import com.tagok.routes_service.service.mapper.PorticoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PorticoService 
{
    private final PorticoRepository porticoRepository;
    private final PorticoMapper porticoMapper;

    public List<PorticoResumenResponse> findAll()
    {
        var porticos = porticoRepository.findAll();

        return porticos.stream()
            .map(porticoMapper::toResumenResponse)
            .toList();
    }

    public PorticoResponse findById(Long id) 
    {
        Portico portico = porticoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portico no encontrado"));

        return porticoMapper.toResponse(portico);
    }
}
