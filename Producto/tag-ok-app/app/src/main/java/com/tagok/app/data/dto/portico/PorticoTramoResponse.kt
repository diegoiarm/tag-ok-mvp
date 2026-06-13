package com.tagok.app.data.dto.portico

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TRAMO")
data class PorticoTramoResponse(
    var id: Long,
    var codigo: String? = null,
    var nombre: String? = null,
    var latitud: Double,
    var longitud: Double,
    var autopista: String,
    var tramos: List<TramoResponse>) : TollResponse()
