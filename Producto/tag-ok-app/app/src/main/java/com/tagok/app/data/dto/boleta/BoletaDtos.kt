package com.tagok.app.data.dto.boleta

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BoletaRequest(
    val patente: String,
    val autopistas: List<String>,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate)

@Serializable
data class BoletaDto(
    val patente: String,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate,
    val items: List<BoletaItemDto>,
    val total: Double)

@Serializable
data class BoletaItemDto(
    val fecha: LocalDate,
    val autopista: String,
    val nombre: String,
    val tipoTarifa: String,
    val valor: Double,
    val horaCruce: LocalDateTime)