package com.tagok.app.data.dto.notificacion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificacionDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("vehiculo_id") val vehiculoId: String? = null,
    @SerialName("tipo") val tipo: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("cuerpo") val cuerpo: String,
    @SerialName("umbral") val umbral: Int? = null,
    @SerialName("porcentaje") val porcentaje: Int? = null,
    @SerialName("periodo") val periodo: String? = null,
    @SerialName("leida") val leida: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class NuevaNotificacionRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("vehiculo_id") val vehiculoId: String? = null,
    @SerialName("tipo") val tipo: String,
    @SerialName("titulo") val titulo: String,
    @SerialName("cuerpo") val cuerpo: String,
    @SerialName("umbral") val umbral: Int? = null,
    @SerialName("porcentaje") val porcentaje: Int? = null,
    @SerialName("periodo") val periodo: String? = null
)

@Serializable
data class MarcarLeidaRequest(
    @SerialName("leida") val leida: Boolean = true
)
