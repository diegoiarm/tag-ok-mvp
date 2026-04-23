package com.tagok.routes_service.domain.portico;

import java.math.BigDecimal;

public record PorticoCruzado(
    Long porticoId,
    String codigo,
    BigDecimal valor) 
{}
