package com.tagok.app.data.dto.presupuesto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresupuestoDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("vehiculo_id") val vehiculoId: String? = null,
    @SerialName("monto_mensual") val montoMensual: Int,
    @SerialName("umbral_alerta_1") val umbralAlerta1: Int,
    @SerialName("umbral_alerta_2") val umbralAlerta2: Int,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class NuevoPresupuestoRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("vehiculo_id") val vehiculoId: String? = null,
    @SerialName("monto_mensual") val montoMensual: Int,
    @SerialName("umbral_alerta_1") val umbralAlerta1: Int,
    @SerialName("umbral_alerta_2") val umbralAlerta2: Int
)

@Serializable
data class ActualizarPresupuestoRequest(
    @SerialName("monto_mensual") val montoMensual: Int,
    @SerialName("umbral_alerta_1") val umbralAlerta1: Int,
    @SerialName("umbral_alerta_2") val umbralAlerta2: Int
)