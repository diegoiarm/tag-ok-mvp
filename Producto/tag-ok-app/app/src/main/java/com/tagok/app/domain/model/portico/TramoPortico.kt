package com.tagok.app.domain.model.portico

data class TramoPortico(
    val entrada: String,
    val salida: String,
    val reglas: List<ReglaTarifaria>,
    val calendario: CalendarioTarifario)