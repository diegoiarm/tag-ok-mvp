package com.tagok.app.domain.interfaces

import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.data.dto.portico.PorticoTramoResponse
import com.tagok.app.domain.model.boleta.Boleta
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.portico.PorticoTipo
import com.tagok.app.domain.model.portico.TollType
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.model.tarifa.TarifaCalculada
import com.tagok.app.domain.model.vehiculo.NuevoVehiculo
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.domain.vehiculo.TipoVehiculo
import io.ktor.client.statement.HttpResponse

interface IBoletaRepository
{
    suspend fun generarBoleta(request: BoletaRequest): Boleta
}

interface IHistoryRepository
{
    suspend fun getAvailableYears(): List<Int>
    suspend fun getResumenAnual(): List<ResumenAnual>
    suspend fun getDetalleAnual(año: Int): ResumenAnual
    suspend fun getDetalleMensual(año: Int, mes: Int): DetalleMensual
    suspend fun getDetalleDia(año: Int, mes: Int, dia: Int): DetalleDia
    suspend fun getPatentes(): List<String>
    suspend fun getAutopistas(): List<String>
    suspend fun getResumenAnualFiltrado(filtro: FiltroHistorialRequest): List<ResumenAnual>
}

interface IPorticoRepository
{
    suspend fun getPorticos(): List<PorticoResumen>
    suspend fun getPorticoById(id: Long): TollType
    suspend fun getPorticoTipo(id: Long): PorticoTipo
    suspend fun getSalidasTramo(id: Long): PorticoTramoResponse
}

interface IRouteRepository
{
    suspend fun getRoute(lon1: Double, lat1: Double, lon2: Double, lat2: Double, vehiculo: TipoVehiculo): Route
}

interface ITarifaRepository
{
    suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculada
}

interface IVehiculoRepository
{
    suspend fun getVehiculos(): List<Vehiculo>
    suspend fun insertVehiculo(nuevo: NuevoVehiculo): HttpResponse
    suspend fun deleteVehiculo(id: String): HttpResponse
}

