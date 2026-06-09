package com.tagok.routes_service.dto.request.portico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Edición de los atributos de un pórtico existente (CU20). Permite reasignar
 * la autopista y cambiar el estado vigente.
 */
public record PorticoUpdateRequest(
    @NotBlank(message = "El código es obligatorio") String codigo,
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    String sentido,
    double latitud,
    double longitud,
    @NotNull(message = "La autopista es obligatoria") Long autopistaId,
    Boolean activo)
{

}
