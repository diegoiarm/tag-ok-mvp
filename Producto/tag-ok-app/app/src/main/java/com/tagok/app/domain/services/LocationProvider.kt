// domain/services/LocationProviderImpl.kt
package com.tagok.app.domain.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.tagok.app.domain.model.location.PorticoGeofence
import com.mapbox.geojson.Point
import com.tagok.app.data.map.GeofenceBroadcastReceiver
import com.tagok.app.domain.services.interfaces.ILocationProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LocationProvider(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geofencingClient: GeofencingClient) : ILocationProvider
{
    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Point> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000
        ).apply {
            setMinUpdateIntervalMillis(3000)
        }.build()

        val locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(result: LocationResult)
            {
                result.lastLocation?.let { location ->
                    trySend(Point.fromLngLat(location.longitude, location.latitude))
                }
            }
        }

        if (hasPermission())
        {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, null)
        }

        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Point?
    {
        if (!hasPermission()) return null

        return try
        {
            val cachedLocation = fusedLocationClient.lastLocation.await()

            if (cachedLocation != null && isLocationFresh(cachedLocation))
            {
                return Point.fromLngLat(cachedLocation.longitude, cachedLocation.latitude)
            }

            val currentLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null).await()

            currentLocation?.let {
                Point.fromLngLat(it.longitude, it.latitude)
            }
        }
        catch (e: Exception)
        {
            null
        }
    }

    private fun isLocationFresh(location: Location): Boolean
    {
        val maxAgeMillis = 2 * 60 * 1000
        return System.currentTimeMillis() - location.time < maxAgeMillis
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun registerGeofences(porticos: List<PorticoGeofence>)
    {
        if (!hasPermission()) return

        val geofences = porticos.map { portico ->
            Geofence.Builder()
                .setRequestId(portico.id)
                .setCircularRegion(
                    portico.location.latitude(),
                    portico.location.longitude(),
                    portico.radius.toFloat())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(30000)
                .build()
        }

        geofencingClient.addGeofences(
            GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofences)
                .build(),
            pendingIntent)
    }

    override fun removeGeofences()
    {
        geofencingClient.removeGeofences(pendingIntent)
    }

    private fun hasPermission() = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}