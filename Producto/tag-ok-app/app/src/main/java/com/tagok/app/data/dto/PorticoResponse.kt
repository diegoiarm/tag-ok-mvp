package com.tagok.app.data.dto

data class PorticoResponse(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val sentido: String,
    val latitud: Double,
    val longitud: Double,
    val autopista: String)
