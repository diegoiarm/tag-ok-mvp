package com.tagok.app.data.repository

import com.tagok.app.data.dto.history.DetalleDiaDTO
import com.tagok.app.data.dto.history.DetalleMensualDTO
import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.data.remote.interfaces.IHistoryApi
import com.tagok.app.domain.interfaces.IHistoryRepository
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual

class HistoryRepository(private val historyApi: IHistoryApi) : IHistoryRepository
{
    override suspend fun getAvailableYears(): List<Int>
    {
        return historyApi.getAvailableYears()
    }

    override suspend fun getResumenAnual(): List<ResumenAnual>
    {
        return historyApi.getResumenAnual().map { it.toDomain() }
    }

    override suspend fun getDetalleAnual(año: Int): ResumenAnual
    {
        return historyApi.getDetalleAnual(año).toDomain()
    }

    override suspend fun getDetalleMensual(año: Int, mes: Int): DetalleMensual
    {
        return historyApi.getDetalleMensual(año, mes).toDomain()
    }

    override suspend fun getDetalleDia(año: Int, mes: Int, dia: Int): DetalleDia
    {
        return historyApi.getDetalleDia(año, mes, dia).toDomain()
    }

    override suspend fun getPatentes(): List<String>
    {
        return historyApi.getPatentes()
    }

    override suspend fun getAutopistas(): List<String>
    {
        return historyApi.getAutopistas()
    }

    override suspend fun getResumenAnualFiltrado(filtro: FiltroHistorialRequest): List<ResumenAnual>
    {
        return historyApi.getResumenAnualFiltrado(filtro).map { it.toDomain() }
    }

    override suspend fun getDetalleMensualFiltrado(
        año: Int,
        mes: Int,
        filtro: FiltroHistorialRequest): DetalleMensual
    {
        return historyApi.getDetalleMensualFiltrado(año, mes,filtro).toDomain()
    }

    override suspend fun getDetalleDiaFiltrado(
        año: Int,
        mes: Int,
        dia: Int,
        filtro: FiltroHistorialRequest): DetalleDia
    {
        return historyApi.getDetalleDiaFiltrado(año, mes, dia, filtro).toDomain()
    }
}