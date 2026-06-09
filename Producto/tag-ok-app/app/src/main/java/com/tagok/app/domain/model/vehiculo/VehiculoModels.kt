package com.tagok.app.domain.model.vehiculo

data class Vehiculo(
    val id: String,
    val userId: String,
    val patente: String,
    val tipoVehiculo: String,
    val numeroTag: String? = null,
    val alias: String? = null,
    val esPrincipal: Boolean = false,
    val createdAt: String? = null)

data class NuevoVehiculo(
    val userId: String,
    val patente: String,
    val tipoVehiculo: String,
    val numeroTag: String? = null,
    val alias: String? = null,
    val esPrincipal: Boolean = false)