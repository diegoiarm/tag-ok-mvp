package com.tagok.app.domain.model

import kotlinx.datetime.LocalDateTime

data class Portico(
    val porticoId: Long,
    val nombre: String,
    val codigo: String,
    val autopista: String,
    val latitud: Double,
    val longitud: Double,
    val tarifa: String,
    val valor: Double,
    val fechaHora: LocalDateTime) : Toll()
