package com.tagok.app.domain.model.presupuesto

data class Presupuesto(
    val id: String,
    val userId: String,
    val vehiculoId: String?,
    val montoMensual: Int,
    val umbralAlerta1: Int,
    val umbralAlerta2: Int,
    val alertasActivas: Boolean,
    val createdAt: String)
