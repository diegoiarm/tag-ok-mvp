package com.tagok.app.domain.model.boleta

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

data class Boleta(
    val patente: String,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate,
    val items: List<BoletaItem>,
    val total: Double)

data class BoletaItem(
    val fecha: LocalDate,
    val autopista: String,
    val nombre: String,
    val tipoTarifa: String,
    val valor: Double,
    val horaCruce: LocalDateTime)