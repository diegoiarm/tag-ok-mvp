package com.tagok.app.domain.model.notificacion

data class Notificacion(
    val id: String,
    val userId: String,
    val vehiculoId: String?,
    val tipo: String,
    val titulo: String,
    val cuerpo: String,
    val umbral: Int?,
    val porcentaje: Int?,
    val periodo: String?,
    val leida: Boolean,
    val createdAt: String?)

/** Alerta recién generada que aún no se ha mostrado como notificación local. */
data class NuevaNotificacion(
    val userId: String,
    val vehiculoId: String?,
    val tipo: String,
    val titulo: String,
    val cuerpo: String,
    val umbral: Int?,
    val porcentaje: Int?,
    val periodo: String?)
{
    companion object
    {
        const val TIPO_PRESUPUESTO_UMBRAL = "PRESUPUESTO_UMBRAL"
    }
}
