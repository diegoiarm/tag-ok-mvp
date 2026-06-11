package com.tagok.app.di.modules

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices

object LocationModule
{
    val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(AppModule.appContext)
    }

    val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(AppModule.appContext)
    }
}