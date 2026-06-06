package com.tagok.app.data.remote.interfaces

import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.dto.boleta.BoletaDto
import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.dto.history.DetalleDiaDTO
import com.tagok.app.data.dto.history.DetalleMensualDTO
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.data.dto.history.ResumenAnualDTO
import com.tagok.app.data.dto.portico.PorticoResumenResponse
import com.tagok.app.data.dto.portico.TollResponse
import com.tagok.app.data.dto.route.RouteRequest
import com.tagok.app.data.dto.route.RouteResponse
import com.tagok.app.data.dto.tarifa.TarifaCalculadaResponse
import com.tagok.app.data.dto.vehiculo.NuevoVehiculoRequest
import com.tagok.app.data.dto.vehiculo.VehiculoDto
import io.ktor.client.statement.HttpResponse

interface IBoletaApi
{
    suspend fun generarBoleta(request: BoletaRequest): BoletaDto
}

interface IHistoryApi
{
    suspend fun getAvailableYears(): List<Int>
    suspend fun getResumenAnual(): List<ResumenAnualDTO>
    suspend fun getDetalleAnual(año: Int): ResumenAnualDTO
    suspend fun getDetalleMensual(año: Int, mes: Int): DetalleMensualDTO
    suspend fun getDetalleDia(año: Int, mes: Int, dia: Int): DetalleDiaDTO
    suspend fun getPatentes(): List<String>
    suspend fun getAutopistas(): List<String>
    suspend fun getResumenAnualFiltrado(filtro: FiltroHistorialRequest): List<ResumenAnualDTO>
}

interface IPorticoApi
{
    suspend fun getPorticos(): List<PorticoResumenResponse>
    suspend fun getPorticoDetails(id: Long): TollResponse
}

interface IRouteApi
{
    suspend fun getRoute(request: RouteRequest): RouteResponse
}

interface ITarifaApi
{
    suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculadaResponse
}

interface IVehiculoApi
{
    suspend fun getVehiculos(): List<VehiculoDto>
    suspend fun insertVehiculo(request: NuevoVehiculoRequest): HttpResponse
    suspend fun deleteVehiculo(id: String): HttpResponse
}