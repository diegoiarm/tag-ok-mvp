package com.tagok.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.tagok.app.data.PorticoResumen
import com.tagok.app.data.RouteApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.double
import kotlinx.serialization.json.Json

data class MapUiState(
    val routePoints: List<Point> = emptyList(),
    val porticos: List<PorticoResumen> = emptyList(),
    val totalCost: Double = 0.0,
    val isLoadingRoute: Boolean = false,
    val error: String? = null,
)

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadPorticos()
    }

    fun calculateRoute(lon1: Double, lat1: Double, lon2: Double, lat2: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRoute = true, error = null, routePoints = emptyList()) }
            runCatching {
                RouteApiService.getRoute(lon1, lat1, lon2, lat2)
            }.onSuccess { response ->
                val points = response.segments.flatMap { parseLineString(it.geometry) }
                _uiState.update {
                    it.copy(
                        routePoints = points,
                        totalCost = response.totalCost,
                        isLoadingRoute = false,
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoadingRoute = false,
                        error = "Error al calcular ruta: ${e.message}",
                    )
                }
            }
        }
    }

    // Coordenadas de prueba: Baquedano → Parque Forestal (Santiago centro)
    fun calculateTestRoute() = calculateRoute(
        lon1 = -70.6354, lat1 = -33.4372,
        lon2 = -70.6483, lat2 = -33.4404,
    )

    private fun loadPorticos() {
        viewModelScope.launch {
            runCatching { RouteApiService.getPorticos() }
                .onSuccess { porticos ->
                    _uiState.update { it.copy(porticos = porticos) }
                }
        }
    }

    // Parsea {"type":"LineString","coordinates":[[lon,lat],...]} → List<Point>
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
}
