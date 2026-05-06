package com.tagok.app.data.local

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LocationEventBus
{
    private val _events = MutableSharedFlow<LocationEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: LocationEvent)
    {
        _events.emit(event)
    }
}

sealed class LocationEvent
{
    data class EnteredGeofence(val porticoId: Long) : LocationEvent()
    data class ExitedGeofence(val porticoId: Long) : LocationEvent()
}