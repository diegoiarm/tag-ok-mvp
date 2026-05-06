package com.tagok.app.data.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError())
            return

        when (geofencingEvent.geofenceTransition)
        {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                val triggeringId: Long? = geofencingEvent.triggeringGeofences
                    ?.firstOrNull()?.requestId?.toLongOrNull()

                triggeringId?.let { porticoId ->
                    CoroutineScope(Dispatchers.Main).launch {
                        LocationEventBus.emit(LocationEvent.EnteredGeofence(porticoId))
                    }
                }
            }
        }
    }
}