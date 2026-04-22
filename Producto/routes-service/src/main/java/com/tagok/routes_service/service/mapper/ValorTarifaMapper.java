package com.tagok.routes_service.service.mapper;

import org.springframework.stereotype.Component;

import com.tagok.routes_service.domain.tarifa.ValorTarifa;
import com.tagok.routes_service.dto.request.ValorTarifaRequest;
import com.tagok.routes_service.dto.response.ValorTarifaResponse;

@Component
public class ValorTarifaMapper implements IEntityMapper<ValorTarifaResponse, ValorTarifaRequest, ValorTarifa>
{
    @Override
    public ValorTarifa fromRequest(ValorTarifaRequest request) 
    {
        return ValorTarifa.builder()
                .tipoTarifa(request.getTipoTarifa())
                .valor(request.getValor())
                .build();
    }

    @Override
    public ValorTarifaResponse toResponse(ValorTarifa entity) 
    {
        return ValorTarifaResponse.builder()
                .tipoTarifa(entity.getTipoTarifa())
                .valor(entity.getValor())
                .build();
    }
    
}
