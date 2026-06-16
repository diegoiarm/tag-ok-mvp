package com.tagok.app.data.remote

import com.tagok.app.data.dto.notificacion.MarcarLeidaRequest
import com.tagok.app.data.dto.notificacion.NotificacionDto
import com.tagok.app.data.dto.notificacion.NuevaNotificacionRequest
import com.tagok.app.data.remote.interfaces.INotificacionApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NotificacionApi(client: HttpClient) : INotificacionApi, ApiClient(client, TAG)
{
    override suspend fun getAll(): List<NotificacionDto> = apiCall("Obtener notificaciones")
    {
        client.get("$BASE_URL/notificacion")
        {
            contentType(ContentType.Application.Json)
            parameter("select", "*")
            parameter("order", "created_at.desc")
        }.body()
    }

    override suspend fun getByPeriodo(
        userId: String,
        periodo: String): List<NotificacionDto> = apiCall("Buscar notificaciones del período $periodo")
    {
        client.get("$BASE_URL/notificacion")
        {
            contentType(ContentType.Application.Json)
            parameter("select", "*")
            parameter("user_id", "eq.$userId")
            parameter("periodo", "eq.$periodo")
        }.body()
    }

    override suspend fun insert(request: NuevaNotificacionRequest) = apiCall("Insertar notificación")
    {
        client.post("$BASE_URL/notificacion")
        {
            contentType(ContentType.Application.Json)
            header("Prefer", "return=minimal")
            setBody(request)
        }
    }

    override suspend fun marcarLeida(id: String) = apiCall("Marcar notificación leída: $id")
    {
        client.patch("$BASE_URL/notificacion")
        {
            contentType(ContentType.Application.Json)
            parameter("id", "eq.$id")
            header("Prefer", "return=minimal")
            setBody(MarcarLeidaRequest(leida = true))
        }
    }

    companion object
    {
        private var BASE_URL = ApiConfig.NOTIFICACION_API
        private const val TAG = "NotificacionApi"
    }
}
