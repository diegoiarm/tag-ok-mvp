package com.tagok.app.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.tagok.app.data.PorticoCruzadoRequest
import com.tagok.app.data.PorticoResumen
import com.tagok.app.data.RouteApiService
import com.tagok.app.data.TarifaCalculada
import com.tagok.app.data.TarifaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDateTime
import kotlin.math.cos
import kotlin.math.sqrt

data class MapUiState(
    val routePoints: List<Point> = emptyList(),
    val porticos: List<PorticoResumen> = emptyList(),
    val porticosCruzados: List<PorticoResumen> = emptyList(),
    val vehiculo: String = "AUTO",
    val tarifaCalculada: TarifaCalculada? = null,
    val totalCost: Double = 0.0,
    val isLoadingRoute: Boolean = false,
    val isLoadingTarifa: Boolean = false,
    val error: String? = null,
)

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init { loadPorticos() }

    fun setVehiculo(vehiculo: String) = _uiState.update { it.copy(vehiculo = vehiculo) }

    fun calculateRoute(lon1: Double, lat1: Double, lon2: Double, lat2: Double) {
        viewModelScope.launch {
            Log.d(TAG, "calculateRoute: ($lat1,$lon1) → ($lat2,$lon2)")
            _uiState.update {
                it.copy(
                    isLoadingRoute = true,
                    error = null,
                    routePoints = emptyList(),
                    tarifaCalculada = null,
                    porticosCruzados = emptyList(),
                )
            }
            runCatching {
                RouteApiService.getRoute(lon1, lat1, lon2, lat2)
            }.onSuccess { response ->
                Log.d(TAG, "getRoute OK — ${response.segments.size} segmentos, costo=${response.totalCost}")
                val points = response.segments.flatMap { parseLineString(it.geometry) }
                val cruzados = findCrossedPorticos(points, _uiState.value.porticos)
                Log.d(TAG, "Pórticos cruzados: ${cruzados.size} — ${cruzados.map { it.codigo }}")
                _uiState.update {
                    it.copy(
                        routePoints = points,
                        totalCost = response.totalCost,
                        porticosCruzados = cruzados,
                        isLoadingRoute = false,
                    )
                }
                if (cruzados.isNotEmpty()) {
                    calculateTarifa(cruzados.map { it.id })
                }
            }.onFailure { e ->
                Log.e(TAG, "getRoute FAIL: ${e::class.simpleName} — ${e.message}", e)
                _uiState.update {
                    it.copy(isLoadingRoute = false, error = "Error al calcular ruta: ${e.message}")
                }
            }
        }
    }

    // Corredor Vespucio Norte — cruza pórticos P1-P3 con tarifas reales
    fun calculateTestRoute() = calculateRoute(-70.7000, -33.5100, -70.8000, -33.4200)

    // Prueba directa de la API de tarifas usando el primer pórtico cargado
    fun calculateTestTarifa() {
        val primer = _uiState.value.porticos.firstOrNull() ?: run {
            _uiState.update { it.copy(error = "No hay pórticos cargados aún") }
            return
        }
        viewModelScope.launch { calculateTarifa(listOf(primer.id)) }
    }

    private suspend fun calculateTarifa(porticoIds: List<Long>) {
        Log.d(TAG, "calculateTarifa: ids=$porticoIds vehiculo=${_uiState.value.vehiculo}")
        _uiState.update { it.copy(isLoadingTarifa = true) }
        val now = LocalDateTime.now().withNano(0).toString()
        val request = TarifaRequest(
            porticosCruzados = porticoIds.map { PorticoCruzadoRequest(it, now) },
            vehiculo = _uiState.value.vehiculo,
        )
        runCatching { RouteApiService.calculateTarifa(request) }
            .onSuccess { tarifa ->
                Log.d(TAG, "calculateTarifa OK — total=${tarifa.total} CLP, cruces=${tarifa.portico.size}")
                _uiState.update { it.copy(tarifaCalculada = tarifa, isLoadingTarifa = false) }
            }
            .onFailure { e ->
                Log.e(TAG, "calculateTarifa FAIL: ${e::class.simpleName} — ${e.message}", e)
                _uiState.update {
                    it.copy(isLoadingTarifa = false, error = "Error al calcular tarifa: ${e.message}")
                }
            }
    }

    private fun loadPorticos() {
        viewModelScope.launch {
            Log.d(TAG, "loadPorticos: iniciando...")
            runCatching { RouteApiService.getPorticos() }
                .onSuccess { p ->
                    Log.d(TAG, "loadPorticos OK — ${p.size} pórticos")
                    _uiState.update { it.copy(porticos = p) }
                }
                .onFailure { e ->
                    Log.e(TAG, "loadPorticos FAIL: ${e::class.simpleName} — ${e.message}", e)
                }
        }
    }

    private fun findCrossedPorticos(
        routePoints: List<Point>,
        porticos: List<PorticoResumen>,
    ): List<PorticoResumen> {
        if (routePoints.isEmpty()) return emptyList()
        return porticos.filter { portico ->
            routePoints.any { point ->
                distanceMeters(
                    point.latitude(), point.longitude(),
                    portico.latitud, portico.longitud,
                ) < THRESHOLD_METERS
            }
        }
    }

    private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = (lat2 - lat1) * 111_000.0
        val dLon = (lon2 - lon1) * 111_000.0 * cos(Math.toRadians(lat1))
        return sqrt(dLat * dLat + dLon * dLon)
    }

    private fun parseLineString(geometryJson: String): List<Point> = runCatching {
        Json.parseToJsonElement(geometryJson)
            .jsonObject["coordinates"]!!
            .jsonArray
            .map { coord ->
                val arr = coord.jsonArray
                Point.fromLngLat(arr[0].jsonPrimitive.double, arr[1].jsonPrimitive.double)
            }
    }.getOrDefault(emptyList())

    fun clearError() = _uiState.update { it.copy(error = null) }

    fun resetMap() = _uiState.update {
        it.copy(
            routePoints = emptyList(),
            porticosCruzados = emptyList(),
            tarifaCalculada = null,
            totalCost = 0.0,
        )
    }

    companion object {
        private const val TAG = "MapViewModel"
        private const val THRESHOLD_METERS = 200.0
    }
}
