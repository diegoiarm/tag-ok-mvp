package com.tagok.app.ui.planificar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.di.ServiceLocator
import com.tagok.app.domain.exceptions.ApplicationError
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.services.PlanificarService
import com.tagok.app.domain.services.interfaces.IPlanificarService
import com.tagok.app.domain.vehiculo.TipoVehiculo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlanificarUiState(
    val singleRoute: Route? = null,
    val routes: List<Route> = emptyList(),
    val vehiculo: TipoVehiculo = TipoVehiculo.AUTO,
    val isLoadingRoute: Boolean = false,
    val error: String? = null)

class PlanificarViajeViewModel(
    private val planificarService: IPlanificarService) : ViewModel()
{
    private val _uiState = MutableStateFlow(PlanificarUiState())
    val uiState: StateFlow<PlanificarUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        val errorMessage = when (exception)
        {
            is ApplicationError -> exception.message ?: "Error desconocido"
            else -> "Error inesperado: ${exception.message}"
        }

        Log.e(TAG, "Error: $errorMessage", exception)

        _uiState.update {
            it.copy(
                error = errorMessage,
                isLoadingRoute = false)
        }
    }

    fun calculateRoute(
        lon1: Double,
        lat1: Double,
        lon2: Double,
        lat2: Double,
        vehiculo: TipoVehiculo = TipoVehiculo.AUTO)
    {
        viewModelScope.launch(exceptionHandler)
        {
            Log.d(TAG, "calculateRoute: solicitando ruta ($lon1, $lat1) -> ($lon2, $lat2) con vehiculo: ${vehiculo.displayName}")

            _uiState.update { it.copy(isLoadingRoute = true, error = null) }

            val route = planificarService.calcularRuta(lon1, lat1, lon2, lat2, vehiculo)

            Log.d(TAG, "calculateRoute: éxito - puntos=${route.points.size}, tolls=${route.tolls.size}, costo=${route.totalCost}")

            _uiState.update {
                it.copy(
                    singleRoute = route,
                    isLoadingRoute = false)
            }

            if (route.points.isEmpty()) Log.w(TAG, "calculateRoute: ruta sin puntos (geometry vacía)")
            if (route.tolls.isEmpty()) Log.w(TAG, "calculateRoute: ruta sin peajes")
        }
    }

    fun resetMap() {
        _uiState.update {
            it.copy(
                singleRoute = null,
                isLoadingRoute = false,
                error = null)
        }
    }

    fun clearError()
    {
        _uiState.update { it.copy(error = null) }
    }

    fun setVehiculo(vehiculo: TipoVehiculo)
    {
        _uiState.update { it.copy(vehiculo = vehiculo) }
    }

    companion object {
        private const val TAG = "PlanificarViajeViewModel"
        val Factory = ServiceLocator.viewModels.planificarViewModelFactory()
    }
}