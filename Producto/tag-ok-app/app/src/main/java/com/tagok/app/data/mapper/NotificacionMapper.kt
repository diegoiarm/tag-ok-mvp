package com.tagok.app.data.mapper

import com.tagok.app.data.dto.notificacion.NotificacionDto
import com.tagok.app.data.dto.notificacion.NuevaNotificacionRequest
import com.tagok.app.domain.model.notificacion.Notificacion
import com.tagok.app.domain.model.notificacion.NuevaNotificacion

fun NotificacionDto.toDomain(): Notificacion = Notificacion(
    id = id,
    userId = userId,
    vehiculoId = vehiculoId,
    tipo = tipo,
    titulo = titulo,
    cuerpo = cuerpo,
    umbral = umbral,
    porcentaje = porcentaje,
    periodo = periodo,
    leida = leida,
    createdAt = createdAt
)

fun NuevaNotificacion.toRequest(): NuevaNotificacionRequest = NuevaNotificacionRequest(
    userId = userId,
    vehiculoId = vehiculoId,
    tipo = tipo,
    titulo = titulo,
    cuerpo = cuerpo,
    umbral = umbral,
    porcentaje = porcentaje,
    periodo = periodo
)
