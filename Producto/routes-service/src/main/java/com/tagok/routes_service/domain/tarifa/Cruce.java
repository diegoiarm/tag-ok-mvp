package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;

public record Cruce(
    Long porticoId,
    String codigo,
    BigDecimal valor) 
{}
