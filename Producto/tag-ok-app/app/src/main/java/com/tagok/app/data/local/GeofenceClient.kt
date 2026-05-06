package com.tagok.app.data.local

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.tagok.app.data.dto.PorticoResumen

class GeofenceClient(private val context: Context)
{
    private val geofencingClient = LocationServices.getGeofencingClient(context)

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun registerPorticoGeofences(porticos: List<PorticoResumen>)
    {
        if (porticos.isEmpty())
            return

        val geofences = porticos.map { portico ->
            Geofence.Builder()
                .setRequestId(portico.id.toString())
                .setCircularRegion(
                    portico.latitud,
                    portico.longitud,
                    150f
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        geofencingClient.addGeofences(request, createPendingIntent())
            .addOnSuccessListener {
                Log.d("GeofenceClient", "Se registraron ${geofences.size} geofences")
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceClient", "Error al registrar geofences", e)
            }
    }

    fun removeAllGeofences()
    {
        geofencingClient.removeGeofences(createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent
    {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}