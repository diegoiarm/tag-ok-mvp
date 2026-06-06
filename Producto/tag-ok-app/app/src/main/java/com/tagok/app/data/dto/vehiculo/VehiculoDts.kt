package com.tagok.app.data.dto.vehiculo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehiculoDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val patente: String,
    @SerialName("tipo_vehiculo") val tipoVehiculo: String,
    @SerialName("numero_tag") val numeroTag: String? = null,
    val alias: String? = null,
    @SerialName("es_principal") val esPrincipal: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null)

@Serializable
data class NuevoVehiculoRequest(
    @SerialName("user_id") val userId: String,
    val patente: String,
    @SerialName("tipo_vehiculo") val tipoVehiculo: String,
    @SerialName("numero_tag") val numeroTag: String? = null,
    val alias: String? = null,
    @SerialName("es_principal") val esPrincipal: Boolean = false)