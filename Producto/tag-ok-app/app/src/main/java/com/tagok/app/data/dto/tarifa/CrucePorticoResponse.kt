package com.tagok.app.data.dto.tarifa

import kotlinx.serialization.Serializable

@Serializable
data class CrucePorticoResponse(
    val porticoId: Long,
    val codigo: String,
    val nombre: String,
    val autopista: String,
    val latitud: Double,
    val longitud: Double,
    val tipoTarifa: String,
    val valor: Double,
    val horaFechaCruce: String) : CruceResponse()