package com.tagok.app.ui.planificar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.RouteApi
import com.tagok.app.data.repository.RouteRepository
import com.tagok.app.domain.model.routes.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlanificarUiState(
    val singleRoute: Route? = null,
    val routes: List<Route> = emptyList(),
    val vehiculo: String = "AUTO",
    val isLoadingRoute: Boolean = false,
    val error: String? = null)

class PlanificarViajeViewModel(private val routeRepository: RouteRepository) : ViewModel()
{
    private val _uiState = MutableStateFlow(PlanificarUiState())
    val uiState: StateFlow<PlanificarUiState> = _uiState.asStateFlow()

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
                singleRoute = null,
                isLoadingRoute = false,
                error = null
            )
        }
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    fun setVehiculo(vehiculo: String)
    {
        _uiState.update { it.copy(vehiculo = vehiculo) }
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
                singleRoute = route,
                isLoadingRoute = false
            )
        }
    }

    /**
     * Futuro: solicita N rutas con distintas opciones en paralelo.
     * Ejemplo: listOf(
     *   RouteOptions(avoidTolls = true),
     *   RouteOptions(vehicleType = "BICICLETA")
     *   Falta implementar desde el api flags para decidir que ruta, hasta
     *   aca la llamada, dejo la idea
     * )
     */
    /*fun calculateMultipleRoutes(
        lon1: Double, lat1: Double,
        lon2: Double, lat2: Double,
        optionsList: List<RouteOptions>)
    {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val results = optionsList.map { options ->
                async {
                    runCatching { routeRepository.getRoute(lon1, lat1, lon2, lat2, options) }
                }
            }.awaitAll()

            // Procesar resultados, filtrar éxitos, manejar errores
            val routes = results.filter { it.isSuccess }.map { it.getOrThrow() }
            val firstError = results.firstOrNull { it.isFailure }?.exceptionOrNull()?.message
            _uiState.update {
                it.copy(
                    multipleRoutes = routes,
                    isLoading = false,
                    error = firstError
                )
            }
        }
    }*/

    companion object {
        private const val TAG = "PlanificarViajeViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val routeApi = RouteApi(HttpClientProvider.client)
                val route = RouteRepository(routeApi)

                return PlanificarViajeViewModel(route) as T
            }
        }
    }
}