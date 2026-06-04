package com.tagok.app.data.repository

import com.tagok.app.data.mapper.toDomain
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual

class HistoryRepository(private val historyApi: HistoryApi)
{
    suspend fun getAvailableYears(usuarioId: String): List<Int>
    {
        return historyApi.getAvailableYears(usuarioId)
    }

    suspend fun getResumenAnual(usuarioId: String): List<ResumenAnual>
    {
        return historyApi.getResumenAnual(usuarioId).map { it.toDomain() }
    }

    suspend fun getDetalleAnual(usuarioId: String, año: Int): ResumenAnual
    {
        return historyApi.getDetalleAnual(usuarioId, año).toDomain()
    }

    suspend fun getDetalleMensual(usuarioId: String, año: Int, mes: Int): DetalleMensual
    {
        return historyApi.getDetalleMensual(usuarioId, año, mes).toDomain()
    }

    suspend fun getDetalleDia(usuarioId: String, año: Int, mes: Int, dia: Int): DetalleDia
    {
        return historyApi.getDetalleDia(usuarioId, año, mes, dia).toDomain()
    }

    suspend fun getPatentes(usuarioId: String): List<String>
    {
        return historyApi.getPatentes(usuarioId)
    }

    suspend fun getResumenAnualFiltrado(usuarioId: String, patentes: List<String>): List<ResumenAnual>
    {
        return historyApi.getResumenAnualFiltrado(usuarioId, patentes).map { it.toDomain() }
    }
}