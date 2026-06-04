// ui/map/MapViewModel.kt
package com.tagok.app.ui.map

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.dto.PorticoCruzadoRequest
import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.data.remote.RouteApi
import com.tagok.app.data.repository.PorticoRepository
import com.tagok.app.data.repository.RouteRepository
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.portico.PorticoTipo
import com.tagok.app.domain.model.tarifa.TarifaCalculada
import com.tagok.app.domain.vehiculo.TipoVehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class MapUiState(
    val porticos: List<PorticoResumen> = emptyList(),
    val tarifaCalculada: TarifaCalculada? = null,
    val isCalculating: Boolean = false,
    val error: String? = null)

class MapViewModel(
    private val porticoRepository: PorticoRepository,
    private val routeRepository: RouteRepository) : ViewModel()
{
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init
    {
        loadPorticos()
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    fun clearTarifa()
    {
        Log.d(TAG, "clearTarifa: limpiando resultado anterior")
        _uiState.update { it.copy(tarifaCalculada = null) }
    }

    fun simularCruceAleatorio(vehiculo: TipoVehiculo, context: Context)
    {
        val porticos = _uiState.value.porticos
        if (porticos.isEmpty())
        {
            Log.w(TAG, "simularCruceAleatorio: no hay pórticos disponibles")
            _uiState.update { it.copy(error = "No hay pórticos disponibles") }
            return
        }

        val porticoAleatorio = porticos.random()
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        Log.d(TAG, "══════════════════════════════════════════")
        Log.d(TAG, "INICIO SIMULACIÓN DE CRUCE")
        Log.d(TAG, "══════════════════════════════════════════")
        Log.d(TAG, "Datos iniciales:")
        Log.d(TAG, "   • Vehículo: ${vehiculo.displayName} (${vehiculo.name})")
        Log.d(TAG, "   • Patente: ABCD-33")
        Log.d(TAG, "   • Fecha/Hora: ${now.format(formatter)}")
        Log.d(TAG, "   • Pórtico aleatorio: id=${porticoAleatorio.id}, nombre=${porticoAleatorio.nombre}")
        Log.d(TAG, "   • Total pórticos disponibles: ${porticos.size}")

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true, error = null) }

            try
            {
                Log.d(TAG, "Consultando tipo de pórtico id=${porticoAleatorio.id}...")
                val tipoPortico = porticoRepository.getPorticoTipo(porticoAleatorio.id)
                Log.d(TAG, "Tipo: $tipoPortico")

                val request = when (tipoPortico)
                {
                    PorticoTipo.PORTICO -> {
                        Log.d(TAG, "Construyendo request para PÓRTICO SIMPLE")

                        val req = TarifaRequest(
                            references = listOf(
                                PorticoCruzadoRequest(
                                    porticoId = porticoAleatorio.id,
                                    porticoHoraFechaCruce = now.format(formatter),
                                    salidaId = null,
                                    salidaHoraFechaCruce = null)),
                            vehiculo = vehiculo.name,
                            patente = "ABCD-33"
                        )

                        Log.d(TAG, "   • Request: $req")
                        req
                    }

                    PorticoTipo.TRAMO -> {
                        Log.d(TAG, "Construyendo request para PÓRTICO DE TRAMO")

                        // Caso especial: P110 (La Pirámide) en AVO1 es un pórtico solitario
                        // que se comporta como PORTICO aunque la autopista sea de tipo TRAMO
                        val porticoTramo = porticoRepository.getSalidasTramo(porticoAleatorio.id)

                        if (porticoTramo.tramos.isEmpty())
                        {
                            Log.w(TAG, "Pórtico especial detectado: ${porticoAleatorio.nombre}")
                            Log.w(TAG, "Este pórtico pertenece a autopista de TRAMO pero no tiene tramos asociados")
                            Log.w(TAG, "Tratándolo como PÓRTICO SIMPLE")

                            TarifaRequest(
                                references = listOf(
                                    PorticoCruzadoRequest(
                                        porticoId = porticoAleatorio.id,
                                        porticoHoraFechaCruce = now.format(formatter),
                                        salidaId = null,
                                        salidaHoraFechaCruce = null)),
                                vehiculo = vehiculo.name,
                                patente = "ABCD-33")
                        }
                        else
                        {
                            Log.d(TAG, "   • Pórtico seleccionado: ${porticoAleatorio.nombre} (id=${porticoAleatorio.id})")
                            Log.d(TAG, "   • Total tramos en respuesta: ${porticoTramo.tramos.size}")

                            // Filtrar tramos donde la entrada coincide con el pórtico seleccionado
                            val tramosDesdeEstePortico = porticoTramo.tramos.filter { tramo ->
                                tramo.nombreEntrada.equals(porticoAleatorio.nombre, ignoreCase = true)
                            }

                            Log.d(TAG, "   • Tramos que COMIENZAN desde este pórtico: ${tramosDesdeEstePortico.size}")

                            if (tramosDesdeEstePortico.isEmpty())
                            {
                                Log.w(TAG, "Este pórtico no tiene tramos como entrada. Buscando como salida...")

                                val tramosHaciaEstePortico = porticoTramo.tramos.filter { tramo ->
                                    tramo.nombreSalida.equals(porticoAleatorio.nombre, ignoreCase = true)
                                }

                                Log.d(TAG, "   • Tramos que TERMINAN en este pórtico: ${tramosHaciaEstePortico.size}")

                                if (tramosHaciaEstePortico.isEmpty())
                                {
                                    Log.e(TAG, " No se encontraron tramos para este pórtico")
                                    throw IllegalStateException("No hay tramos disponibles para ${porticoAleatorio.nombre}")
                                }

                                val tramoAleatorio = tramosHaciaEstePortico.random()
                                Log.d(TAG, "   • Tramo seleccionado (como salida): ${tramoAleatorio.nombreEntrada} → ${tramoAleatorio.nombreSalida}")

                                val porticoEntrada = porticos.find {
                                    it.nombre.equals(tramoAleatorio.nombreEntrada, ignoreCase = true)
                                }

                                if (porticoEntrada == null)
                                {
                                    Log.e(TAG, "No se encontró pórtico de entrada: ${tramoAleatorio.nombreEntrada}")
                                    throw IllegalStateException("Pórtico de entrada no encontrado: ${tramoAleatorio.nombreEntrada}")
                                }

                                Log.d(TAG, "Pórtico de entrada: ${porticoEntrada.nombre} (id=${porticoEntrada.id})")
                                Log.d(TAG, "Pórtico de salida: ${porticoAleatorio.nombre} (id=${porticoAleatorio.id})")

                                TarifaRequest(
                                    references = listOf(
                                        PorticoCruzadoRequest(
                                            porticoId = porticoEntrada.id,
                                            porticoHoraFechaCruce = now.format(formatter),
                                            salidaId = porticoAleatorio.id,
                                            salidaHoraFechaCruce = now.plusMinutes(15).format(formatter)
                                        )
                                    ),
                                    vehiculo = vehiculo.name,
                                    patente = "ABCD-33"
                                )
                            }
                            else
                            {
                                val tramoAleatorio = tramosDesdeEstePortico.random()
                                Log.d(TAG, "   • Tramo seleccionado (como entrada): ${tramoAleatorio.nombreEntrada} → ${tramoAleatorio.nombreSalida}")

                                // Buscar pórtico de SALIDA
                                val porticoSalida = porticos.find {
                                    it.nombre.equals(tramoAleatorio.nombreSalida, ignoreCase = true)
                                }

                                if (porticoSalida == null) {
                                    Log.e(TAG, "No se encontró pórtico de salida: ${tramoAleatorio.nombreSalida}")
                                    Log.e(TAG, "   Pórticos disponibles:")
                                    porticos.take(10).forEach { p ->
                                        Log.e(TAG, "      id=${p.id}, nombre='${p.nombre}'")
                                    }
                                    throw IllegalStateException("Pórtico de salida no encontrado: ${tramoAleatorio.nombreSalida}")
                                }

                                Log.d(TAG, "Pórtico de entrada: ${porticoAleatorio.nombre} (id=${porticoAleatorio.id})")
                                Log.d(TAG, "Pórtico de salida: ${porticoSalida.nombre} (id=${porticoSalida.id})")

                                TarifaRequest(
                                    references = listOf(
                                        PorticoCruzadoRequest(
                                            porticoId = porticoAleatorio.id,
                                            porticoHoraFechaCruce = now.format(formatter),
                                            salidaId = porticoSalida.id,
                                            salidaHoraFechaCruce = now.plusMinutes(15).format(formatter))),
                                    vehiculo = vehiculo.name,
                                    patente = "ABCD-33"
                                )
                            }
                        }
                    }
                }

                Log.d(TAG, "Enviando request a calculateTarifa...")
                val response = routeRepository.calculateTarifa(request)

                // 4. Analizar respuesta
                Log.d(TAG, "══════════════════════════════════════════")
                Log.d(TAG, "RESPUESTA RECIBIDA")
                Log.d(TAG, "══════════════════════════════════════════")
                Log.d(TAG, "   • Total: ${response.total}")
                Log.d(TAG, "   • Vehículo: ${response.vehiculo}")
                Log.d(TAG, "   • Cantidad de cruces: ${response.cruces.size}")

                response.cruces.forEachIndexed { index, cruce ->
                    Log.d(TAG, "   ┌─ Cruce #${index + 1}")
                    Log.d(TAG, "   │  • Código: ${cruce.codigo}")
                    Log.d(TAG, "   │  • Nombre: ${cruce.nombre}")
                    Log.d(TAG, "   │  • Autopista: ${cruce.autopista}")
                    Log.d(TAG, "   │  • Tipo Tarifa: ${cruce.tipoTarifa}")
                    Log.d(TAG, "   │  • Valor: ${cruce.valor}")
                    Log.d(TAG, "   │  • Hora/Fecha: ${cruce.horaFechaCruce}")

                    when (cruce)
                    {
                        is com.tagok.app.domain.model.tarifa.CrucePortico -> {
                            Log.d(TAG, "   │  • Tipo: PORTICO")
                            Log.d(TAG, "   │  • PorticoId: ${cruce.porticoId}")
                        }
                        is com.tagok.app.domain.model.tarifa.CruceTramo -> {
                            Log.d(TAG, "   │  • Tipo: TRAMO")
                            Log.d(TAG, "   │  • Entrada: ${cruce.nombreEntrada} (id=${cruce.entradaId})")
                            Log.d(TAG, "   │  • Salida: ${cruce.nombreSalida} (id=${cruce.salidaId})")
                        }
                    }
                    Log.d(TAG, "   └──────────────────────────────")
                }

                val crucesGratis = response.cruces.filter { it.valor == 0.0 }
                if (crucesGratis.isNotEmpty())
                {
                    Log.w(TAG, "ALERTA: ${crucesGratis.size} cruce(s) con valor \$0 detectado(s)")
                    crucesGratis.forEach { cruce ->
                        Log.w(TAG, "   → ${cruce.nombre} (${cruce.tipoTarifa}) - Hora: ${cruce.horaFechaCruce}")
                        Log.w(TAG, "     ¿Posible causa? Tarifa no encontrada para tipo ${response.vehiculo} a las ${cruce.horaFechaCruce}")
                    }
                }

                Log.d(TAG, "Simulación completada exitosamente")

                _uiState.update {
                    it.copy(
                        tarifaCalculada = response,
                        isCalculating = false)
                }

                NotificationUtils.agregarMultiplesCruces(context, response)

            }
            catch (e: Exception)
            {
                Log.e(TAG, "══════════════════════════════════════════")
                Log.e(TAG, "ERROR EN SIMULACIÓN")
                Log.e(TAG, "══════════════════════════════════════════")
                Log.e(TAG, "   • Mensaje: ${e.message}")
                Log.e(TAG, "   • Tipo: ${e.javaClass.simpleName}")
                Log.e(TAG, "   • StackTrace inicial:")
                e.stackTrace?.take(5)?.forEach { line ->
                    Log.e(TAG, "      $line")
                }

                _uiState.update {
                    it.copy(
                        error = "Error al calcular tarifa: ${e.message}",
                        isCalculating = false)
                }
            }
        }
    }

    fun limpiarNotificaciones()
    {
        Log.d(TAG, "limpiarNotificaciones: reiniciando acumulador")
        NotificationUtils.limpiarAcumulador()
    }

    private fun loadPorticos()
    {
        viewModelScope.launch {
            Log.d(TAG, "══════════════════════════════════════════")
            Log.d(TAG, "CARGANDO PÓRTICOS")
            Log.d(TAG, "══════════════════════════════════════════")

            runCatching {
                porticoRepository.getPorticos()
            }.onSuccess { list ->
                Log.d(TAG, "${list.size} pórticos cargados")
                list.take(5).forEach { p ->
                    Log.d(TAG, "   • id=${p.id}, nombre='${p.nombre}'")
                }
                if (list.size > 5)
                {
                    Log.d(TAG, "   • ... y ${list.size - 5} más")
                }
                _uiState.update { it.copy(porticos = list) }
            }.onFailure { e ->
                Log.e(TAG, "Error cargando pórticos: ${e.message}", e)
                _uiState.update { it.copy(error = "Error cargando pórticos: ${e.message}") }
            }
        }
    }

    companion object
    {
        private const val TAG = "MapViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory
        {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val routeApi = RouteApi(HttpClientProvider.client)
                val routeRepo = RouteRepository(routeApi)

                val porticoApi = PorticoApi(HttpClientProvider.client)
                val porticoRepo = PorticoRepository(porticoApi)

                return MapViewModel(porticoRepo, routeRepo) as T
            }
        }
    }
}