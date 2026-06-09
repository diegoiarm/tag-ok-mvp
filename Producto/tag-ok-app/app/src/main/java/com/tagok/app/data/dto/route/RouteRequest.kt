package com.tagok.app.data.dto.route

import com.tagok.app.domain.vehiculo.TipoVehiculo
import kotlinx.serialization.Serializable

@Serializable
data class RouteRequest(
    val lon1: Double,
    val lat1: Double,
    val lon2: Double,
    val lat2: Double,
    val vehiculo: TipoVehiculo)
