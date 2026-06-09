package com.tagok.app.data.dto.tarifa

import kotlinx.serialization.Serializable

@Serializable
data class TarifaCalculadaResponse(
    val total: Double,
    val cruces: List<CruceResponse>,
    val vehiculo: String)