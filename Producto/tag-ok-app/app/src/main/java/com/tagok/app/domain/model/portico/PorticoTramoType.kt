package com.tagok.app.domain.model.portico

data class PorticoTramoType(
    var id: Long,
    var codigo: String,
    var nombre: String,
    var latitud: Double,
    var longitud: Double,
    var autopista: String,
    var tramos: List<TramoPortico>) : TollType()
