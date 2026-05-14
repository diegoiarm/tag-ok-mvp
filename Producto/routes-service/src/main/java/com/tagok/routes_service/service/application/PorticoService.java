package com.tagok.routes_service.service.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.autopista.TipoCobro;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.dto.response.portico.PorticoResponse;
import com.tagok.routes_service.dto.response.portico.TollResponse;
import com.tagok.routes_service.repository.PorticoRepository;
import com.tagok.routes_service.service.mapper.PorticoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PorticoService 
{
    private final PorticoRepository porticoRepository;
    private final PorticoMapper porticoMapper;

    public List<TollResponse> findAll()
    {
        return porticoRepository.findAll().stream()
            .filter(p -> p.getAutopista().getTipoCobro() == TipoCobro.TRAMO || (p.getCalendario() != null && !p.getReglas().isEmpty()))
            .map(p -> 
            {
                if (p.getAutopista().getTipoCobro() == TipoCobro.TRAMO)
                    return porticoMapper.toTramoResponse(p);

                return porticoMapper.toResponse(p);
            })
            .toList();
    }

    public PorticoResponse findById(Long id) 
    {
        Portico portico = porticoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Portico no encontrado"));

        return porticoMapper.toResponse(portico);
    }
}
