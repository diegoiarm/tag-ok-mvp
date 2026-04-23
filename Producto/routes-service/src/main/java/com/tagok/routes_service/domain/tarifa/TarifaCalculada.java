package com.tagok.routes_service.domain.tarifa;

import java.math.BigDecimal;
import java.util.List;

import com.tagok.routes_service.domain.vehiculo.TipoVehiculo;

public record TarifaCalculada(
    BigDecimal total,
    List<Cruce> portico,
    TipoVehiculo vehiculo) 
{}