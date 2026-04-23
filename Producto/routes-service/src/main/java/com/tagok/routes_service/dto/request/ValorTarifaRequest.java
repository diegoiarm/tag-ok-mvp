package com.tagok.routes_service.dto.request;

import java.math.BigDecimal;

import com.tagok.routes_service.domain.tarifa.TipoTarifa;

public record ValorTarifaRequest(
    TipoTarifa tipoTarifa,
    BigDecimal valor) 
{

}
