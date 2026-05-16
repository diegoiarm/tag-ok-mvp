package com.tagok.app.domain.model.portico

data class TramoType(
    val entrada: String,
    val salida: String,
    val reglas: List<ReglaTarifaria>,
    val calendario: CalendarioTarifario) : TollType()