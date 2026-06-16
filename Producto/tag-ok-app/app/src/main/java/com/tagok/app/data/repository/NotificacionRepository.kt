package com.tagok.app.data.repository

import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.mapper.toRequest
import com.tagok.app.data.remote.interfaces.INotificacionApi
import com.tagok.app.domain.interfaces.INotificacionRepository
import com.tagok.app.domain.model.notificacion.Notificacion
import com.tagok.app.domain.model.notificacion.NuevaNotificacion

class NotificacionRepository(private val api: INotificacionApi) : INotificacionRepository
{
    override suspend fun getAll(): List<Notificacion> =
        api.getAll().map { it.toDomain() }

    override suspend fun getByPeriodo(userId: String, periodo: String): List<Notificacion> =
        api.getByPeriodo(userId, periodo).map { it.toDomain() }

    override suspend fun crear(nueva: NuevaNotificacion)
    {
        api.insert(nueva.toRequest())
    }

    override suspend fun marcarLeida(id: String)
    {
        api.marcarLeida(id)
    }
}
