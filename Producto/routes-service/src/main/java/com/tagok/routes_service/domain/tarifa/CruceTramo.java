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
    String codigoEntrada,
    String codigoSalida,
    Long entradaId,
    Long salidaId,
    String nombreEntrada,
    String nombreSalida,
    double latitudEntrada,
    double longitudEntrada,
    double latitudSalida,
    double longitudSalida) implements Cruce {}
