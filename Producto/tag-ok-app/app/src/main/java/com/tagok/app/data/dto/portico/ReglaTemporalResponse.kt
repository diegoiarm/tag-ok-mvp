package com.tagok.app.data.dto.portico

import kotlinx.serialization.Serializable

@Serializable
data class ReglaTemporalResponse(
    val tipoTarifa: String,
    val tipoDia: String,
    val tramos: List<RangoHorarioResponse>)
