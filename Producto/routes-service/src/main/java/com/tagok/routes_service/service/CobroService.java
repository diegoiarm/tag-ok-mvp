package com.tagok.routes_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.Portico;
import com.tagok.routes_service.domain.dto.request.CobroRequest;
import com.tagok.routes_service.domain.dto.request.PorticoCobroRequest;
import com.tagok.routes_service.domain.dto.response.CobroResponse;
import com.tagok.routes_service.repository.PorticoRepository;
import com.tagok.routes_service.service.mapper.PorticoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CobroService 
{
    private final PorticoRepository porticoRepository;
    private final PorticoMapper porticoMapper;

    public CobroResponse calcularCobro(CobroRequest cobro)
    {
        var porticos = findAllByIds(cobro.getPorticos());
        
        var resumenes = porticos.stream()
        .map(porticoMapper::toResumenResponse)
        .toList();

        var response = CobroResponse.builder()
            .porticos(resumenes)
            .build();

        return response;
    }

    private List<Portico> findAllByIds(List<PorticoCobroRequest> porticos)
    {
        if (porticos == null || porticos.isEmpty())
            return List.of();

        var ids = porticos.stream()
            .map(PorticoCobroRequest::getId)
            .filter(id -> id != null)
            .distinct()
            .toList();

        var porticosEncontrados = porticoRepository.findAllById(ids);

        if (porticosEncontrados.size() != ids.size()) 
        {
            var idsEncontrados = porticosEncontrados.stream()
                .map(Portico::getId)
                .toList();

            var idsFaltantes = ids.stream()
                .filter(id -> !idsEncontrados.contains(id))
                .toList();

            throw new IllegalArgumentException("No se encontraron los pórticos con IDs: " + idsFaltantes);
        }

        return porticosEncontrados;
    }
}