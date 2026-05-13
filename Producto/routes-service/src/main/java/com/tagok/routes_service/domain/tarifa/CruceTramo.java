package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CruceTramo(
    String codigo,
    String nombre,
    String autopista,
    TipoTarifa tipoTarifa,
    BigDecimal valor,
    LocalDateTime horaFechaCruce,
    Long entradaId,
    Long salidaId,
    String nombreEntrada,
    String nombreSalida) implements Cruce {}
