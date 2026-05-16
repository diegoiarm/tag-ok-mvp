package com.tagok.app.data.dto.portico

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TramoResponse(
    val entrada: String,
    val nombreEntrada: String,
    val salida: String,
    val nombreSalida: String,
    val reglas: List<ReglaTarifariaResponse>,
    val calendario: CalendarioTarifarioResponse)
