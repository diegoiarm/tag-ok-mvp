package com.tagok.app.domain.model.portico

data class PorticoType(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val sentido: String,
    val latitud: Double,
    val longitud: Double,
    val autopista: String,
    val reglas: List<ReglaTarifaria>,
    val calendario: CalendarioTarifario) : TollType()
