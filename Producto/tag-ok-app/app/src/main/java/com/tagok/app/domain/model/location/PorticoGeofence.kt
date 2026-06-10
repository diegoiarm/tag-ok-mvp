package com.tagok.app.domain.model.location

import com.mapbox.geojson.Point

data class PorticoGeofence(
    val id: String,
    val location: Point,
    val radius: Double = 100.0)
