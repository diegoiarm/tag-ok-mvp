package com.tagok.app.data.repository

import com.tagok.app.data.dto.PorticoResumen
import com.tagok.app.data.dto.TarifaCalculada
import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.RouteApi
import com.tagok.app.domain.interfaces.IRouteRepository
import com.tagok.app.domain.model.Route

class RouteRepository(
    private val api: RouteApi) : IRouteRepository
{
    override suspend fun getRoute(
        lon1: Double,
        lat1: Double,
        lon2: Double,
        lat2: Double): Route {
        return api.getRoute(lon1, lat1, lon2, lat2).toDomain()
    }

    override suspend fun getPorticos(): List<PorticoResumen>
    {
        return api.getPorticos()
    }

    override suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculada
    {
        return api.calculateTarifa(request)
    }
}