package com.tagok.app.data.dto.portico

import kotlinx.serialization.Serializable

@Serializable
data class PorticoResumenResponse(
    val id: Long,
    val nombre: String,
    val latitud: Double,
    val longitud: Double)