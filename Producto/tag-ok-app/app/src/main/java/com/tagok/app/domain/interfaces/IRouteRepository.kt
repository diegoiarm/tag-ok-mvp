package com.tagok.app.domain.interfaces

import com.tagok.app.data.dto.route.PorticoResumen
import com.tagok.app.data.dto.TarifaCalculada
import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.domain.model.routes.Route

interface IRouteRepository
{
    suspend fun getRoute(lon1: Double, lat1: Double, lon2: Double, lat2: Double): Route
    suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculada
}