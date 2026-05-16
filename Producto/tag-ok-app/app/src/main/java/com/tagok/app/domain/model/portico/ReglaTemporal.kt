package com.tagok.app.domain.model.portico

data class ReglaTemporal(
    val tipoTarifa: String,
    val tipoDia: String,
    val tramos: List<RangoHorario>)
