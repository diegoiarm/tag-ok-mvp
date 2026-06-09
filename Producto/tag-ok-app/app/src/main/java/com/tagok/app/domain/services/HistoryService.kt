package com.tagok.app.domain.services

import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.domain.interfaces.IHistoryRepository
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.domain.services.interfaces.IHistoryService

class HistoryService(private val historyRepository: IHistoryRepository) : IHistoryService, ApplicationService()
{
    override suspend fun getAvaliableYears(): List<Int> = execute("Obteniendo los años disponibles")
    {
        historyRepository.getAvailableYears()
    }

    override suspend fun getPatentes(): List<String> = execute("Obteniendo patentes disponibles")
    {
        historyRepository.getPatentes()
    }

    override suspend fun getAutopistas(): List<String> = execute("Obteniendo autopistas disponibles")
    {
        historyRepository.getAutopistas()
    }

    override suspend fun getResumenAnual(): List<ResumenAnual> = execute("Obteniendo resumenes anuales")
    {
        historyRepository.getResumenAnual()
    }

    override suspend fun getDetalleAnual(año: Int): ResumenAnual = execute("obteniendo detalle para año $año")
    {
        historyRepository.getDetalleAnual(año)
    }

    override suspend fun getDetalleMensual(
        año: Int,
        mes: Int): DetalleMensual = execute("Obteniendo detalle mensual de mes: $mes, año: $año")
    {
        historyRepository.getDetalleMensual(año, mes)
    }

    override suspend fun getDetalleDiario(
        año: Int,
        mes: Int,
        dia: Int): DetalleDia = execute("Obteniendo detalle diario año: $año, mes: $mes, dia: $dia")
    {
        historyRepository.getDetalleDia(año, mes, dia)
    }

    override suspend fun getResumenAnualFiltrado(filtro: FiltroHistorialRequest): List<ResumenAnual> =
        execute("Obteniendo el resumen filtrado: { patentes: ${filtro.patentes.size}, autopistas: ${filtro.autopistas.size} }")
        {
            historyRepository.getResumenAnualFiltrado(filtro)
        }

    override suspend fun getDetalleMensualFiltrado(
        año: Int,
        mes: Int,
        filtro: FiltroHistorialRequest): DetalleMensual = execute("Obteniendo detalle mensual de mes: $mes, año: $año filtrado")
    {
        historyRepository.getDetalleMensualFiltrado(año, mes, filtro)
    }

    override suspend fun getDetalleDiaFiltrado(
        año: Int,
        mes: Int,
        dia: Int,
        filtro: FiltroHistorialRequest): DetalleDia = execute("Obteniendo detalle diario año: $año, mes: $mes, dia: $dia filtrado")
    {
        historyRepository.getDetalleDiaFiltrado(año, mes, dia, filtro)
    }

}