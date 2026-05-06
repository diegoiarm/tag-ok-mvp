package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TarifaCalculada(
    val total: Double,
    val portico: List<Cruce>,   // campo "portico" en el backend
    val vehiculo: String)
