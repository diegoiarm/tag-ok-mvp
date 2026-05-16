package com.tagok.app.data.dto.portico

import kotlinx.serialization.Serializable

@Serializable
data class CalendarioTarifarioResponse(
    val reglas: List<ReglaTemporalResponse>)
