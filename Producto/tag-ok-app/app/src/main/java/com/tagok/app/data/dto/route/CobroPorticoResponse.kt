package com.tagok.app.data.dto.route

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CobroPorticoResponse(
    val porticoId: Long?,
    val nombre: String?,
    val codigo: String?,
    val autopista: String,
    val latitud: Double,
    val longitud: Double,
    val tarifa: String,
    val valor: Double,
    val fechaHora: LocalDateTime) : CobroRutaResponse()
