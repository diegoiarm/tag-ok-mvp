package com.tagok.app.domain.services

import com.tagok.app.domain.interfaces.IRouteRepository
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.services.interfaces.IPlanificarService
import com.tagok.app.domain.vehiculo.TipoVehiculo

class PlanificarService(private val routeRepository: IRouteRepository) : IPlanificarService, ApplicationService()
{
    override suspend fun calcularRuta(
        lon1: Double,
        lat1: Double,
        lon2: Double,
        lat2: Double,
        vehiculo: TipoVehiculo): Route = execute("Calcular ruta: ($lon1,$lat1) a ($lon2,$lat2) - vehiculo: $vehiculo")
    {
        routeRepository.getRoute(lon1, lat1, lon2, lat2, vehiculo)
    }
}