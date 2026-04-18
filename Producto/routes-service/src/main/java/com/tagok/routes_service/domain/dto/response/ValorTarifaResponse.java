package com.tagok.routes_service.domain.dto.response;

import com.tagok.routes_service.domain.TipoTarifa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValorTarifaResponse 
{
    private TipoTarifa tipoTarifa;
    private double valor;
}
