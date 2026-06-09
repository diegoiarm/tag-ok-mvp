package com.tagok.app.domain.services.interfaces

import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.domain.model.boleta.Boleta
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual
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

interface IHistoryService
{
    suspend fun getAvaliableYears(): List<Int>
    suspend fun getPatentes(): List<String>
    suspend fun getAutopistas(): List<String>
    suspend fun getResumenAnual(): List<ResumenAnual>
    suspend fun getDetalleAnual(año: Int): ResumenAnual
    suspend fun getDetalleMensual(año: Int, mes: Int): DetalleMensual
    suspend fun getDetalleDiario(año: Int,mes: Int, dia: Int): DetalleDia
    suspend fun getResumenAnualFiltrado(filtro: FiltroHistorialRequest): List<ResumenAnual>
    suspend fun getDetalleMensualFiltrado(año: Int, mes: Int, filtro: FiltroHistorialRequest): DetalleMensual
    suspend fun getDetalleDiaFiltrado(año: Int, mes: Int, dia: Int, filtro: FiltroHistorialRequest): DetalleDia
}