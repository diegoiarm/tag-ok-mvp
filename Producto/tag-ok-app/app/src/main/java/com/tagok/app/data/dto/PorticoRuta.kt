package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PorticoRuta(
    val nombre: String? = "",
    val codigo: String = "",
    val sentido: String = "",
    val autopista: String = "",
    val codigoAutopista: String? = "",
    val longitud: Double = 0.0,
    val latitud: Double = 0.0,
    val tarifa: String = "",
    val valor: Double = 0.0)