package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PorticoResumen(
    val id: Long,
    val codigo: String,
    val sentido: String? = null,
    val latitud: Double,
    val longitud: Double)
