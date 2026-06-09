package com.tagok.app.domain.services

import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.domain.exceptions.ApplicationError
import com.tagok.app.domain.interfaces.IBoletaRepository
import com.tagok.app.domain.interfaces.IHistoryRepository
import com.tagok.app.domain.model.boleta.Boleta
import com.tagok.app.domain.services.interfaces.IBoletaService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class BoletaService(
    private val boletaRepository: IBoletaRepository,
    private val historyRepository: IHistoryRepository) : IBoletaService, ApplicationService()
{
    override suspend fun generarBoleta(request: BoletaRequest): Boleta =
        execute("Generar boleta")
        {
            boletaRepository.generarBoleta(request)
        }

    suspend fun cargarDatosIniciales(): DatosBoleta = execute("Cargar datos iniciales")
    {
        coroutineScope {
            val patentesDeferred = async { historyRepository.getPatentes() }
            val autopistasDeferred = async { historyRepository.getAutopistas() }

            val patentes = patentesDeferred.await()
            val autopistas = autopistasDeferred.await()

            val (fechaDesde, fechaHasta) = calcularFechasPorDefecto()
            val patentePorDefecto = if (patentes.isNotEmpty()) patentes.first() else ""

            DatosBoleta(
                patentes = patentes,
                autopistas = autopistas,
                fechaDesde = fechaDesde,
                fechaHasta = fechaHasta,
                patentePorDefecto = patentePorDefecto)
        }
    }

    suspend fun generarBoletaValidada(
        patente: String,
        autopistasSeleccionadas: List<String>,
        fechaDesde: LocalDate,
        fechaHasta: LocalDate): Boleta = execute("Generar boleta validada")
    {
        validarParametros(patente, fechaDesde, fechaHasta)

        val request = BoletaRequest(
            patente = patente,
            autopistas = autopistasSeleccionadas,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta)

        boletaRepository.generarBoleta(request)
    }

    private fun validarParametros(
        patente: String,
        fechaDesde: LocalDate,
        fechaHasta: LocalDate)
    {
        when
        {
            patente.isBlank() ->
                throw ApplicationError.Validation("Seleccione una patente")
            fechaDesde > fechaHasta ->
                throw ApplicationError.Validation("La fecha desde debe ser menor o igual a la fecha hasta")
        }
    }

    private fun calcularFechasPorDefecto(): Pair<LocalDate, LocalDate>
    {
        val hoy = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        val desde = LocalDate(
            year = hoy.year,
            monthNumber = hoy.monthNumber,
            dayOfMonth = 5).minus(1, DateTimeUnit.MONTH)

        val hasta = LocalDate(
            year = hoy.year,
            monthNumber = hoy.monthNumber,
            dayOfMonth = 5)

        return Pair(desde, hasta)
    }
}

data class DatosBoleta(
    val patentes: List<String>,
    val autopistas: List<String>,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate,
    val patentePorDefecto: String)