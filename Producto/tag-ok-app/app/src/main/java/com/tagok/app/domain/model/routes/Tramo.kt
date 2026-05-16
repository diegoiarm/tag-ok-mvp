package com.tagok.app.domain.model.routes

import kotlinx.datetime.LocalDateTime

data class Tramo(
    val entradaId: Long,
    val salidaId: Long,
    val codigoEntrada: String,
    val codigoSalida: String,
    val nombreEntrada: String,
    val nombreSalida: String,
    val autopista: String,
    val latitudEntrada: Double,
    val longitudEntrada: Double,
    val latitudSalida: Double,
    val longitudSalida: Double,
    val tarifa: String,
    val valor: Double,
    val fechaHora: LocalDateTime) : Toll()
