package com.tagok.app.data.dto.tarifa

import kotlinx.serialization.Serializable

@Serializable
data class CruceTramoResponse(
    val codigo: String,
    val nombre: String,
    val autopista: String,
    val tipoTarifa: String,
    val valor: Double,
    val horaFechaCruce: String,
    val codigoEntrada: String,
    val codigoSalida: String,
    val entradaId: Long,
    val salidaId: Long,
    val nombreEntrada: String,
    val nombreSalida: String,
    val latitudEntrada: Double,
    val longitudEntrada: Double,
    val latitudSalida: Double,
    val longitudSalida: Double) : CruceResponse()