package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;

public record Tarifa(
    BigDecimal monto,
    TipoTarifa tipoTarifa) 
{
}
