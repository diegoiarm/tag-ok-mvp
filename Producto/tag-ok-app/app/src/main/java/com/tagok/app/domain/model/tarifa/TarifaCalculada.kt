package com.tagok.app.domain.model.tarifa

data class TarifaCalculada(
    val total: Double,
    val cruces: List<Cruce>,
    val vehiculo: String)