package com.tagok.app.data.dto.portico

import kotlinx.serialization.Serializable

@Serializable
data class PorticoResumenResponse(
    val id: Long,
    val latitud: Double,
    val longitud: Double)