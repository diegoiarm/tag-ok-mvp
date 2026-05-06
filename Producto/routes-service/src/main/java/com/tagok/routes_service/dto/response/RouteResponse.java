package com.tagok.routes_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse 
{
    private List<PorticoRouteResponse> porticos;
    private BigDecimal totalCost;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String mergedRouteGeometry;
}
