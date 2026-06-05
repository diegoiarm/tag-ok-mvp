package com.tagok.app.data.repository

import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual

class HistoryRepository(private val historyApi: HistoryApi)
{
    suspend fun getAvailableYears(): List<Int>
    {
        return historyApi.getAvailableYears()
    }

    suspend fun getResumenAnual(): List<ResumenAnual>
    {
        return historyApi.getResumenAnual().map { it.toDomain() }
    }

    suspend fun getDetalleAnual(año: Int): ResumenAnual
    {
        return historyApi.getDetalleAnual(año).toDomain()
    }

    suspend fun getDetalleMensual(año: Int, mes: Int): DetalleMensual
    {
        return historyApi.getDetalleMensual(año, mes).toDomain()
    }

    suspend fun getDetalleDia(año: Int, mes: Int, dia: Int): DetalleDia
    {
        return historyApi.getDetalleDia(año, mes, dia).toDomain()
    }

    suspend fun getPatentes(): List<String>
    {
        return historyApi.getPatentes()
    }

    suspend fun getAutopistas(): List<String>
    {
        return historyApi.getAutopistas()
    }

    suspend fun getResumenAnualFiltrado(filtro: FiltroHistorialRequest): List<ResumenAnual>
    {
        return historyApi.getResumenAnualFiltrado(filtro).map { it.toDomain() }
    }
}