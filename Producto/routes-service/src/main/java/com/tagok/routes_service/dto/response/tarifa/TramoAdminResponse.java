package com.tagok.routes_service.dto.response.tarifa;

import lombok.Builder;

/**
 * Resumen de un tramo para la gestión administrativa de tarifas (CU19).
 */
@Builder
public record TramoAdminResponse(
    Long id,
    String entradaCodigo,
    String entradaNombre,
    String salidaCodigo,
    String salidaNombre,
    Long autopistaId,
    String autopistaNombre,
    double distanciaKm,
    boolean tieneTarifa)
{
}
