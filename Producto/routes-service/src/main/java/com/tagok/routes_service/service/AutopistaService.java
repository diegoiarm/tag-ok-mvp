package com.tagok.routes_service.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tagok.routes_service.domain.Autopista;
import com.tagok.routes_service.domain.Portico;
import com.tagok.routes_service.domain.dto.request.AutopistaRequest;
import com.tagok.routes_service.domain.dto.response.AutopistaResponse;
import com.tagok.routes_service.repository.AutopistaRepository;

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

        Set<String> codigosExistentes = autopista.getPorticos().stream()
                .map(Portico::getCodigo)
                .collect(Collectors.toSet());

        List<Portico> porticosNuevos = request.getPorticos().stream()
                .filter(pr -> !codigosExistentes.contains(pr.getCodigo()))
                .map(pr -> 
                {
                    Portico portico = porticoMapper.fromRequest(pr);
                    portico.setAutopista(autopista);
                    return portico;
                })
                .toList();

        autopista.getPorticos().addAll(porticosNuevos);

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
