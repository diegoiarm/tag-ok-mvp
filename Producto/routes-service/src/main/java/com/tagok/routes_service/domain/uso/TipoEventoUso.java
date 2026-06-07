package com.tagok.routes_service.domain.uso;

/** Tipo de interacción de uso registrada para la reportería operativa (CU18). */
public enum TipoEventoUso
{
    /** Cálculo de una ruta (POST /v1/rutas). */
    CONSULTA_RUTA,
    /** Estimación de tarifa de un cruce (POST /v1/tarifas). */
    ESTIMACION_TARIFA
}
