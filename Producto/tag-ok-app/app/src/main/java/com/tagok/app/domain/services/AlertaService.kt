package com.tagok.app.domain.services

import com.tagok.app.data.dto.history.FiltroHistorialRequest
import com.tagok.app.domain.interfaces.IHistoryRepository
import com.tagok.app.domain.interfaces.INotificacionRepository
import com.tagok.app.domain.interfaces.IPresupuestoRepository
import com.tagok.app.domain.interfaces.IVehiculoRepository
import com.tagok.app.domain.model.notificacion.NuevaNotificacion
import com.tagok.app.domain.model.presupuesto.Presupuesto
import com.tagok.app.domain.model.vehiculo.Vehiculo
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.NumberFormat
import java.util.Locale

/**
 * CU14/CU15 (Opción A): evalúa en el cliente si el gasto del mes cruzó alguno de
 * los umbrales de un presupuesto con alertas activas y, de ser así, crea la
 * notificación (deduplicada por mes+umbral) para que el llamador la muestre.
 */
class AlertaService(
    private val presupuestoRepository: IPresupuestoRepository,
    private val vehiculoRepository: IVehiculoRepository,
    private val historyRepository: IHistoryRepository,
    private val notificacionRepository: INotificacionRepository)
{
    suspend fun revisarYNotificar(userId: String): List<NuevaNotificacion>
    {
        val hoy = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        val año = hoy.year
        val mes = hoy.monthNumber
        val periodo = "%04d-%02d".format(año, mes)

        val presupuestos = runCatching { presupuestoRepository.getAll() }
            .getOrDefault(emptyList())
            .filter { it.alertasActivas && it.montoMensual > 0 }
        if (presupuestos.isEmpty()) return emptyList()

        val vehiculos = runCatching { vehiculoRepository.getVehiculos() }.getOrDefault(emptyList())
        val yaNotificadas = runCatching { notificacionRepository.getByPeriodo(userId, periodo) }
            .getOrDefault(emptyList())

        val nuevas = mutableListOf<NuevaNotificacion>()

        for (presupuesto in presupuestos)
        {
            val gasto = gastoDelMes(presupuesto, vehiculos, año, mes) ?: continue
            val porcentaje = ((gasto / presupuesto.montoMensual) * 100).toInt()

            val umbrales = listOf(presupuesto.umbralAlerta1, presupuesto.umbralAlerta2)
                .filter { it in 1..100 }
                .distinct()
                .sorted()

            for (umbral in umbrales)
            {
                if (porcentaje < umbral) continue

                val yaExiste = yaNotificadas.any {
                    it.vehiculoId == presupuesto.vehiculoId &&
                        it.umbral == umbral &&
                        it.tipo == NuevaNotificacion.TIPO_PRESUPUESTO_UMBRAL
                }
                if (yaExiste) continue

                val nueva = construir(userId, presupuesto, vehiculos, umbral, porcentaje, gasto, periodo)
                runCatching { notificacionRepository.crear(nueva) }
                    .onSuccess { nuevas += nueva }
            }
        }
        return nuevas
    }

    private suspend fun gastoDelMes(
        presupuesto: Presupuesto,
        vehiculos: List<Vehiculo>,
        año: Int,
        mes: Int): Double?
    {
        return runCatching {
            if (presupuesto.vehiculoId == null)
            {
                historyRepository.getDetalleMensual(año, mes).totalMes
            }
            else
            {
                val patente = vehiculos.find { it.id == presupuesto.vehiculoId }?.patente
                    ?: return null
                historyRepository.getDetalleMensualFiltrado(
                    año, mes, FiltroHistorialRequest(patentes = listOf(patente))
                ).totalMes
            }
        }.getOrNull()
    }

    private fun construir(
        userId: String,
        presupuesto: Presupuesto,
        vehiculos: List<Vehiculo>,
        umbral: Int,
        porcentaje: Int,
        gasto: Double,
        periodo: String): NuevaNotificacion
    {
        val ambito = if (presupuesto.vehiculoId == null) "Global"
        else vehiculos.find { it.id == presupuesto.vehiculoId }
            ?.let { it.alias?.takeIf { a -> a.isNotBlank() } ?: it.patente }
            ?: "tu vehículo"

        val titulo = "Alerta de presupuesto · $umbral%"
        val cuerpo = "Superaste el $umbral% del presupuesto $ambito. " +
            "Gasto del mes: ${gasto.toCLP()} de ${presupuesto.montoMensual.toCLP()} ($porcentaje%)."

        return NuevaNotificacion(
            userId = userId,
            vehiculoId = presupuesto.vehiculoId,
            tipo = NuevaNotificacion.TIPO_PRESUPUESTO_UMBRAL,
            titulo = titulo,
            cuerpo = cuerpo,
            umbral = umbral,
            porcentaje = porcentaje,
            periodo = periodo)
    }

    private fun Number.toCLP(): String
    {
        val fmt = NumberFormat.getNumberInstance(Locale("es", "CL"))
        fmt.minimumFractionDigits = 0
        fmt.maximumFractionDigits = 0
        return "$${fmt.format(this)}"
    }
}
