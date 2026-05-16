package com.tagok.app.data.dto.route

import kotlinx.serialization.Serializable

@Serializable
data class RouteResponse(
    val totalCost: Double,
    val cobros: List<CobroRutaResponse>,
    val mergedRouteGeometry: String? = null)