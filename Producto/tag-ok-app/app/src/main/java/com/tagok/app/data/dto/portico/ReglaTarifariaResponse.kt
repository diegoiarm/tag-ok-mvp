package com.tagok.app.data.dto.portico

import kotlinx.serialization.Serializable

@Serializable
data class ReglaTarifariaResponse(
    val aplicaA: List<String>,
    val valores: List<ValorTarifaResponse>)
