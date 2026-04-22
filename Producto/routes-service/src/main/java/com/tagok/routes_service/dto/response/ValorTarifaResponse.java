package com.tagok.routes_service.dto.response;

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
public class ValorTarifaResponse 
{
    private TipoTarifa tipoTarifa;
    private BigDecimal valor;
}
