package com.tagok.routes_service.dto.request.autopista;

import jakarta.validation.constraints.NotBlank;

/**
 * Datos editables de una concesionaria. El tipo de cobro no se incluye a
 * propósito: cambiarlo afectaría la estrategia de cálculo de tarifas.
 */
public record AutopistaUpdateRequest(
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    @NotBlank(message = "El código es obligatorio") String codigo)
{

}
