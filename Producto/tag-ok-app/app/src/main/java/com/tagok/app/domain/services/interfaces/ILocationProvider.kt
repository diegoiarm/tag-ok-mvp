package com.tagok.app.domain.services.interfaces

import com.tagok.app.domain.model.location.PorticoGeofence
import com.tagok.app.domain.model.routes.Point
import kotlinx.coroutines.flow.Flow

interface ILocationProvider
{
    fun getLocationUpdates(): Flow<com.mapbox.geojson.Point>
    suspend fun getCurrentLocation(): com.mapbox.geojson.Point?
    fun registerGeofences(porticos: List<PorticoGeofence>)
    fun removeGeofences()
}