package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Cruce(
    Long porticoId,
    String codigo,
    String nombre,
    String autopista,
    TipoTarifa tarifa,
    BigDecimal valor,
    LocalDateTime horaFechaCruce) 
{}
