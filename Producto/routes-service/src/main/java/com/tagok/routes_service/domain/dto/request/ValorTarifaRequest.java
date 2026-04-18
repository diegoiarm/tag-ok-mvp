package com.tagok.routes_service.domain.dto.request;

import com.tagok.routes_service.domain.TipoTarifa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValorTarifaRequest 
{
    private TipoTarifa tipoTarifa;
    private double valor;
}
