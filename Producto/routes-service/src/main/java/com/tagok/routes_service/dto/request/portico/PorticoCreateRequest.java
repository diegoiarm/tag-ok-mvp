package com.tagok.routes_service.dto.request.portico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Alta manual de un pórtico desde el panel de administración (CU20).
 * El pórtico siempre se asocia a una autopista (concesionaria) existente.
 */
public record PorticoCreateRequest(
    @NotBlank(message = "El código es obligatorio") String codigo,
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    String sentido,
    double latitud,
    double longitud,
    @NotNull(message = "La autopista es obligatoria") Long autopistaId)
{

}
