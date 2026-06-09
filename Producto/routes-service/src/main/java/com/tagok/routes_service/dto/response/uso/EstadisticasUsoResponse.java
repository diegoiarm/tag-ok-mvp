package com.tagok.routes_service.dto.response.uso;

import java.util.List;

import lombok.Builder;

/** Estadísticas agregadas de uso de producto para la reportería (CU18). */
@Builder
public record EstadisticasUsoResponse(
    long totalConsultasRutas,
    long totalEstimaciones,
    long consultasRutasUltimos30Dias,
    long estimacionesUltimos30Dias,
    List<PuntoMensual> porMes)
{
    @Builder
    public record PuntoMensual(
        String mes,
        long consultasRutas,
        long estimaciones) {}
}
