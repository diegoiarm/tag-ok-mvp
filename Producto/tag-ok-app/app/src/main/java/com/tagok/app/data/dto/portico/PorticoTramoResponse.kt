package com.tagok.app.data.dto.portico

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TRAMO")
data class PorticoTramoResponse(
    var id: Long,
    var codigo: String,
    var nombre: String,
    var latitud: Double,
    var longitud: Double,
    var autopista: String,
    var tramos: List<TramoResponse>) : TollResponse()
