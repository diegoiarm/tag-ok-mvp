package com.tagok.routes_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tagok.routes_service.domain.tarifa.TipoTarifa;

public record PorticoRouteResponse(
    String nombre,
    String codigo,
    String autopista,
    String codigoAutopista,
    String sentido,
    double longitud,
    double latitud,
    TipoTarifa tarifa,
    BigDecimal valor,
    LocalDateTime fechaHora
) 
{

}
