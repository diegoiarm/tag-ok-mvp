package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RouteResponse(
    val totalCost: Double,
    val porticos: List<PorticoRuta> = emptyList(),
    val mergedRouteGeometry: String? = null)