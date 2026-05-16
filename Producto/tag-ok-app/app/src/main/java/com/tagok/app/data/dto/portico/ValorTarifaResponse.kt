package com.tagok.app.data.dto.portico

import kotlinx.serialization.Serializable

@Serializable
data class ValorTarifaResponse(
    val tipoTarifa: String,
    val valor: Double)
