package com.tagok.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RouteSegment(
    val seq: Int = 0,
    val edgeId: Long = 0,
    val node: Long = 0,
    val cost: Double = 0.0,
    val aggCost: Double = 0.0,
    val name: String? = null,
    val geometry: String = "")   // JSON string: {"type":"LineString","coordinates":[[lon,lat],...]}