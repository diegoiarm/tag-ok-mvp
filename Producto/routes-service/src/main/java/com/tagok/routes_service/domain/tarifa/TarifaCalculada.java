package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tagok.routes_service.domain.portico.Cruce;

public record TarifaCalculada(
    BigDecimal total,
    Cruce portico,
    LocalDateTime fechaHora) 
{}