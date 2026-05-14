package com.tagok.routes_service.dto.response.route;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record RouteResponse(
    LocalDateTime fechaHoraInicio,
    LocalDateTime fechaHoraFin,
    BigDecimal totalCost,
    List<CobroRutaResponse> cobros,
    String mergedRouteGeometry) 
{

}
