package com.tagok.app.data.dto.route

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CobroTramoResponse(
    val entradaId: Long,
    val salidaId: Long,
    val nombreEntrada: String,
    val nombreSalida: String,
    val autopista: String,
    val latitudEntrada: Double,
    val longitudEntrada: Double,
    val latitudSalida: Double,
    val longitudSalida: Double,
    val tarifa: String,
    val valor: Double,
    val fechaHora: LocalDateTime) : CobroRutaResponse()