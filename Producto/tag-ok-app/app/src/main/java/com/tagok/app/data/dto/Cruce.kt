package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Cruce(
    val porticoId: Long,
    val codigo: String,
    val nombre: String? = null,
    val autopista: String? = null,
    val tarifa: String? = null,
    val valor: Double = 0.0)
    // horaFechaCruce omitido — solo necesitamos los valores monetarios
