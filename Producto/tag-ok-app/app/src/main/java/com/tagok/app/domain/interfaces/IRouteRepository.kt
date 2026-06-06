package com.tagok.app.domain.interfaces

import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.model.tarifa.TarifaCalculada
import com.tagok.app.domain.vehiculo.TipoVehiculo

interface IRouteRepository
{
    suspend fun getRoute(lon1: Double, lat1: Double, lon2: Double, lat2: Double, vehiculo: TipoVehiculo): Route
}