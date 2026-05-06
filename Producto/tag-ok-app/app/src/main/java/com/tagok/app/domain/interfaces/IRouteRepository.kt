package com.tagok.app.domain.interfaces

import com.tagok.app.data.dto.PorticoResumen
import com.tagok.app.data.dto.TarifaCalculada
import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.domain.model.Portico
import com.tagok.app.domain.model.Route

interface IRouteRepository
{
    suspend fun getRoute(lon1: Double, lat1: Double, lon2: Double, lat2: Double): Route
    suspend fun getPorticos(): List<PorticoResumen>
    suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculada
}