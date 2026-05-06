package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TarifaRequest(
    val porticosCruzados: List<PorticoCruzadoRequest>,
    val vehiculo: String)
