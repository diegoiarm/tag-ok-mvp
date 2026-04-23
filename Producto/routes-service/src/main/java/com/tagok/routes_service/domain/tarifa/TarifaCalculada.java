package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TarifaCalculada(
    BigDecimal total,
    Cruce portico,
    LocalDateTime fechaHora) 
{}