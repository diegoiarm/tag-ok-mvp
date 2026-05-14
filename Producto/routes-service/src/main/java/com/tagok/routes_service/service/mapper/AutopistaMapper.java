package com.tagok.routes_service.service.mapper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.autopista.Autopista;
import com.tagok.routes_service.domain.portico.Portico;
import com.tagok.routes_service.domain.tramo.Tramo;
import com.tagok.routes_service.dto.request.autopista.AutopistaRequest;
import com.tagok.routes_service.dto.response.AutopistaResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutopistaMapper implements IEntityMapper<AutopistaResponse, AutopistaRequest, Autopista>
{
    private final TramoMapper tramoMapper;
    private final PorticoMapper porticoMapper;

    public Autopista fromRequest(AutopistaRequest request) 
    {
        Autopista autopista = Autopista.builder()
            .nombre(request.autopista())
            .codigo(request.codigo())
            .tipoCobro(request.tipoCobro())
            .build();

        Optional.ofNullable(request.porticos())
            .ifPresent(porticosReq -> porticosReq.forEach(pr ->
                autopista.addPortico(porticoMapper.fromRequest(pr))
            ));

        // 2. Construir un mapa código -> Portico con los pórticos recién añadidos
        Map<String, Portico> porticosMap = autopista.getPorticos().stream()
            .collect(Collectors.toMap(Portico::getCodigo, Function.identity()));

        Optional.ofNullable(request.tramos())
            .ifPresent(tramosReq -> tramosReq.forEach(tr -> 
            {
                Portico entrada = Optional.ofNullable(porticosMap.get(tr.entrada()))
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Pórtico entrada no encontrado: " + tr.entrada()));
                Portico salida = Optional.ofNullable(porticosMap.get(tr.salida()))
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Pórtico salida no encontrado: " + tr.salida()));

                Tramo tramo = tramoMapper.fromRequest(tr, entrada, salida);
                autopista.addTramo(tramo);
            }));

        return autopista;
    }

    @Override
    public AutopistaResponse toResponse(Autopista autopista)
    {
        return AutopistaResponse.builder()
            .id(autopista.getId())
            .nombre(autopista.getNombre())
            .codigo(autopista.getCodigo())
            .tipoCobro(autopista.getTipoCobro())
            .porticos(autopista.getPorticos().stream()
                .map(porticoMapper::toResponse)
                .toList())
            .tramos(autopista.getTramos().stream()
                .map(tramoMapper::toResponse)
                .toList())
            .build();
    }
}
