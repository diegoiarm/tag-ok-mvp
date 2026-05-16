package com.tagok.app.domain.model.portico

data class TramoPortico(
    val entrada: String,
    val nombreEntrada: String,
    val salida: String,
    val nombreSalida: String,
    val reglas: List<ReglaTarifaria>,
    val calendario: CalendarioTarifario)