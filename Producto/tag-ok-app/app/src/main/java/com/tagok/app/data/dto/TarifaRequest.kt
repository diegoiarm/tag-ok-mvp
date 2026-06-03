package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TarifaRequest(
    val references: List<PorticoCruzadoRequest>,
    val vehiculo: String,
    val patente: String
)