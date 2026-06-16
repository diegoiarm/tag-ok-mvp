package com.tagok.app.domain.services

import com.tagok.app.data.dto.boleta.BoletaRequest
import com.tagok.app.data.remote.exceptions.ApiException
import com.tagok.app.domain.exceptions.ApplicationError
import com.tagok.app.domain.interfaces.IBoletaRepository
import com.tagok.app.domain.interfaces.IHistoryRepository
import com.tagok.app.domain.interfaces.IVehiculoRepository
import com.tagok.app.domain.model.boleta.ArchivoFactura
import com.tagok.app.domain.model.boleta.Boleta
import com.tagok.app.domain.model.boleta.ComparacionFactura
import com.tagok.app.domain.model.vehiculo.Vehiculo
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
    private val historyRepository: IHistoryRepository,
    private val vehiculoRepository: IVehiculoRepository) : IBoletaService, ApplicationService()
{
    override suspend fun generarBoleta(request: BoletaRequest): Boleta =
        execute("Generar boleta")
        {
            boletaRepository.generarBoleta(request)
        }

    suspend fun cargarDatosIniciales(): DatosBoleta = execute("Cargar datos iniciales")
    {
        coroutineScope {
            // Mismos vehículos registrados que en Home/Presupuesto, no solo los que
            // ya tienen cruces facturables en el historial
            val vehiculosDeferred = async { vehiculoRepository.getVehiculos() }
            val autopistasDeferred = async { historyRepository.getAutopistas() }

            val vehiculos = vehiculosDeferred.await()
            val autopistas = autopistasDeferred.await()

            val (fechaDesde, fechaHasta) = calcularFechasPorDefecto()
            val vehiculoPorDefecto = vehiculos.firstOrNull { it.esPrincipal } ?: vehiculos.firstOrNull()

            DatosBoleta(
                vehiculos = vehiculos,
                autopistas = autopistas,
                fechaDesde = fechaDesde,
                fechaHasta = fechaHasta,
                patentePorDefecto = vehiculoPorDefecto?.patente ?: "")
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

    suspend fun compararFacturaValidada(
        patente: String,
        autopistasSeleccionadas: List<String>,
        fechaDesde: LocalDate,
        fechaHasta: LocalDate,
        archivo: ArchivoFactura): ComparacionFactura = execute("Comparar factura")
    {
        validarParametros(patente, fechaDesde, fechaHasta)
        validarArchivo(archivo)

        val request = BoletaRequest(
            patente = patente,
            autopistas = autopistasSeleccionadas,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta)

        try
        {
            boletaRepository.compararFactura(request, archivo)
        }
        catch (e: ApiException)
        {
            // 400/502/503 traen el mensaje del backend (archivo inválido, IA sin
            // configurar, cuota de Gemini agotada): mostrarlo tal cual al usuario
            val mensaje = e.message
            if (e.statusCode in MOSTRAR_MENSAJE_BACKEND && !mensaje.isNullOrBlank())
            {
                throw ApplicationError.Validation(mensaje)
            }
            throw e
        }
    }

    private fun validarArchivo(archivo: ArchivoFactura)
    {
        when
        {
            archivo.bytes.isEmpty() ->
                throw ApplicationError.Validation("El archivo adjunto está vacío")
            archivo.bytes.size > MAX_ARCHIVO_BYTES ->
                throw ApplicationError.Validation(
                    "El archivo supera el límite de 10 MB (${archivo.tamanoLegible})")
        }
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

    companion object
    {
        private const val MAX_ARCHIVO_BYTES = 10 * 1024 * 1024
        private val MOSTRAR_MENSAJE_BACKEND = setOf(400, 502, 503)
    }
}

data class DatosBoleta(
    val vehiculos: List<Vehiculo>,
    val autopistas: List<String>,
    val fechaDesde: LocalDate,
    val fechaHasta: LocalDate,
    val patentePorDefecto: String)