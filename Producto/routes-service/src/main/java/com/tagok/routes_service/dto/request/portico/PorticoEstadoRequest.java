package com.tagok.routes_service.dto.request.portico;

import jakarta.validation.constraints.NotNull;

/** Cambia el estado vigente/desactivado de un pórtico (CU20). */
public record PorticoEstadoRequest(
    @NotNull(message = "El estado es obligatorio") Boolean activo)
{

}
