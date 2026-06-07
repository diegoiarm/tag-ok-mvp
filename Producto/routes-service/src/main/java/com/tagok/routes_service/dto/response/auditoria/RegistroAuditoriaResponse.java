package com.tagok.routes_service.dto.response.auditoria;

import java.time.LocalDateTime;

import com.tagok.routes_service.domain.auditoria.TipoAccion;

import lombok.Builder;

/** Entrada de auditoría para la reportería admin (CU18). */
@Builder
public record RegistroAuditoriaResponse(
    Long id,
    TipoAccion accion,
    String entidad,
    String entidadId,
    String descripcion,
    String usuarioId,
    String usuarioEmail,
    LocalDateTime fecha)
{
}
