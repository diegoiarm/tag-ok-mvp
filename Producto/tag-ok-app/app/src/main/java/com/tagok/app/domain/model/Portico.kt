package com.tagok.app.domain.model

data class Portico(
    val codigo: String,
    val nombre: String?,
    val sentido: String,
    val autopista: String,
    val latitud: Double,
    val longitud: Double,
    val tarifa: String,
    val valor: Double)
