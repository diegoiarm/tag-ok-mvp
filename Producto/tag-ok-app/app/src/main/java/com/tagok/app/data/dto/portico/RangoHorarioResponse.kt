package com.tagok.app.data.dto.portico

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class RangoHorarioResponse(
    val horaInicio: LocalTime,
    val horaFin: LocalTime)
