package com.tagok.app.domain.services.interfaces

import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.domain.model.boleta.Boleta
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.vehiculo.TipoVehiculo

interface IBoletaService
{
    suspend fun generarBoleta(request: BoletaRequest): Boleta
}

interface IPlanificarService
{
    suspend fun calcularRuta(
        lon1: Double,
        lat1: Double,
        lon2: Double,
        lat2: Double,
        vehiculo: TipoVehiculo = TipoVehiculo.AUTO): Route
}

interface IPorticoService
{
    suspend fun obtenerPorticos(): List<PorticoResumen>
}