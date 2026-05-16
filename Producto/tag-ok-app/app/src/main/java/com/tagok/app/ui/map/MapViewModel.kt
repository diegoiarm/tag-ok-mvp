package com.tagok.app.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.dto.PorticoResumen
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.data.remote.RouteApi
import com.tagok.app.data.repository.PorticoRepository
import com.tagok.app.data.repository.RouteRepository
import com.tagok.app.domain.model.routes.Route
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapUiState(
    val route: Route? = null,
    val porticos: List<PorticoResumen> = emptyList(),
    val vehiculo: String = "AUTO",
    val isLoadingRoute: Boolean = false,
    val error: String? = null
)

class MapViewModel(
    private val routeRepository: RouteRepository,
    private val porticoRepository: PorticoRepository) : ViewModel()
{
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init
    {
        loadPorticos()
    }

    fun setVehiculo(vehiculo: String)
    {
        _uiState.update { it.copy(vehiculo = vehiculo) }
    }

    fun calculateRoute(
        lon1: Double,
        lat1: Double,
        lon2: Double,
        lat2: Double)
    {
        viewModelScope.launch {
            Log.d(TAG, "calculateRoute: solicitando ruta ($lon1, $lat1) -> ($lon2, $lat2)")
            setLoadingRoute(true)
            setError(null)

            runCatching {
                routeRepository.getRoute(lon1, lat1, lon2, lat2)
            }.onSuccess { route ->
                Log.d(TAG, "calculateRoute: éxito - puntos=${route.points.size}, tolls=${route.tolls.size}, costo=${route.totalCost}")
                setRoute(route)

                if (route.points.isEmpty()) Log.w(TAG, "calculateRoute: ruta sin puntos (geometry vacía)")
                if (route.tolls.isEmpty()) Log.w(TAG, "calculateRoute: ruta sin peajes")
            }.onFailure { e ->
                Log.e(TAG, "calculateRoute: fallo -> ${e.message}", e)
                setLoadingRoute(false)
                setError(e.message)
            }
        }
    }

    fun resetMap()
    {
        _uiState.update {
            it.copy(
                route = null,
                isLoadingRoute = false,
                error = null
            )
        }
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadPorticos()
    {
        viewModelScope.launch {

            Log.d(TAG, "loadPorticos: iniciando...")

            runCatching {
                porticoRepository.getPorticos()
            }.onSuccess { list ->

                Log.d(TAG, "loadPorticos OK — ${list.size} pórticos")

                _uiState.update {
                    it.copy(porticos = list)
                }

            }.onFailure { e ->
                Log.e(TAG, "loadPorticos FAIL: ${e.message}", e)
            }
        }
    }

    private fun setLoadingRoute(value: Boolean)
    {
        _uiState.update { it.copy(isLoadingRoute = value) }
    }

    private fun setError(message: String?)
    {
        _uiState.update { it.copy(error = message) }
    }

    private fun setRoute(route: Route)
    {
        _uiState.update {
            it.copy(
                route = route,
                isLoadingRoute = false
            )
        }
    }

    companion object {
        private const val TAG = "MapViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val routeApi = RouteApi(HttpClientProvider.client)
                val porticoApi = PorticoApi(HttpClientProvider.client)
                val portico = PorticoRepository(porticoApi)
                val route = RouteRepository(routeApi)

                return MapViewModel(route, portico) as T
            }
        }
    }
}