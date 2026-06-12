package com.tagok.app.data.dto.portico

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PORTICO")
data class PorticoResponse(
    val id: Long,
    val codigo: String? = null,
    val nombre: String? = null,
    val sentido: String? = null,
    val latitud: Double,
    val longitud: Double,
    val autopista: String,
    val reglas: List<ReglaTarifariaResponse>,
    val calendario: CalendarioTarifarioResponse) : TollResponse()
