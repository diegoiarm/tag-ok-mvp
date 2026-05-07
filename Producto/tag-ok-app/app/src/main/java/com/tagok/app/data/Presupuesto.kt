package com.tagok.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Presupuesto(
    val id: String = "",
    @SerialName("user_id")         val userId: String = "",
    @SerialName("vehiculo_id")     val vehiculoId: String? = null,
    @SerialName("monto_mensual")   val montoMensual: Int = 0,
    @SerialName("umbral_alerta_1") val umbralAlerta1: Int = 75,
    @SerialName("umbral_alerta_2") val umbralAlerta2: Int = 90,
    @SerialName("created_at")      val createdAt: String? = null,
)

@Serializable
data class NuevoPresupuesto(
    @SerialName("user_id")         val userId: String,
    @SerialName("vehiculo_id")     val vehiculoId: String? = null,
    @SerialName("monto_mensual")   val montoMensual: Int,
    @SerialName("umbral_alerta_1") val umbralAlerta1: Int = 75,
    @SerialName("umbral_alerta_2") val umbralAlerta2: Int = 90,
)

@Serializable
internal data class ActualizarPresupuesto(
    @SerialName("monto_mensual")   val montoMensual: Int,
    @SerialName("umbral_alerta_1") val umbralAlerta1: Int,
    @SerialName("umbral_alerta_2") val umbralAlerta2: Int,
)
