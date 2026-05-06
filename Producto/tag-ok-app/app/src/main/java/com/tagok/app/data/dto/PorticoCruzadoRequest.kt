package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PorticoCruzadoRequest(
    val porticoId: Long,
    val horaFechaCruce: String)   // ISO-8601: "2026-04-23T14:30:00"
