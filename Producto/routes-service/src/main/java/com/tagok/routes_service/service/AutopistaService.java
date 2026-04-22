package com.tagok.routes_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.dto.request.AutopistaRequest;
import com.tagok.routes_service.dto.response.AutopistaResponse;
import com.tagok.routes_service.repository.AutopistaRepository;
import com.tagok.routes_service.service.mapper.AutopistaMapper;
import com.tagok.routes_service.service.mapper.PorticoMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutopistaService 
{
    private final AutopistaRepository autopistaRepository;
    private final AutopistaMapper autopistaMapper;
    private final PorticoMapper porticoMapper;

    @Transactional
    public AutopistaResponse saveAutopistaWithPorticos(AutopistaRequest request) 
    {
        Autopista autopista = autopistaRepository.findByNombre(request.getAutopista())
                .orElseGet(() -> autopistaMapper.fromRequest(request));

        request.getPorticos().forEach(porticoRequest -> 
        {
            Portico portico = porticoMapper.fromRequest(porticoRequest);
            autopista.addPortico(portico);
        });

        Autopista autopistaGuardada = autopistaRepository.save(autopista);
        return autopistaMapper.toResponse(autopistaGuardada);
    }

    public List<AutopistaResponse> findAll()
    {
        var autopistas = autopistaRepository.findAll();

        return autopistas.stream()
            .map(autopista -> autopistaMapper.toResponse(autopista))
            .toList();
    }

    public void deleteById(Long id)
    {
        autopistaRepository.deleteById(id);
    }
}
