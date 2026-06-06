package com.tagok.app.data.repository

import android.util.Log
import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.dto.route.RouteRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.RouteApi
import com.tagok.app.domain.interfaces.IRouteRepository
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.model.tarifa.TarifaCalculada
import com.tagok.app.domain.vehiculo.TipoVehiculo

class RouteRepository(private val api: RouteApi) : IRouteRepository
{
    override suspend fun getRoute(
        lon1: Double,
        lat1: Double,
        lon2: Double,
        lat2: Double,
        vehiculo: TipoVehiculo): Route
    {
        Log.d(TAG, "getRoute: llamando API con ($lon1, $lat1) -> ($lon2, $lat2) y vehiculo: ${vehiculo.displayName}")

        val response = api.getRoute(RouteRequest(lon1, lat1, lon2, lat2, vehiculo))

        Log.d(TAG, "getRoute: respuesta cruda = $response")

        val domain = response.toDomain()

        Log.d(TAG, "getRoute: dominio mapeado -> puntos=${domain.points.size}, tolls=${domain.tolls.size}, costo=${domain.totalCost}")
        return domain
    }

    companion object {
        private const val TAG = "RouteRepository"
    }
}