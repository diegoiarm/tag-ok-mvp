package com.tagok.routes_service.dto.request;

import java.math.BigDecimal;

import com.tagok.routes_service.domain.tarifa.TipoTarifa;

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
    private BigDecimal valor;
}
