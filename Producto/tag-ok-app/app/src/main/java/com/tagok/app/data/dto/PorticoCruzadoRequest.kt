package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PorticoCruzadoRequest(
    val porticoId: Long,
    val porticoHoraFechaCruce: String,
    val salidaId: Long?,
    val salidaHoraFechaCruce: String?)
