package com.tagok.history_service.dto;

public enum EstadoComparacion
{
    /** El cruce aparece en ambos documentos con el mismo valor. */
    COINCIDE,
    /** El cruce aparece en ambos documentos pero con valores distintos. */
    MONTO_DIFERENTE,
    /** Cruce registrado por la app que no aparece en la factura del cliente. */
    SOLO_EN_APP,
    /** Cruce cobrado en la factura del cliente que la app no registró. */
    SOLO_EN_FACTURA
}
