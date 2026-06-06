package com.tagok.app.ui.vehiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.VehiculoApi
import com.tagok.app.data.repository.VehiculoRepository
import com.tagok.app.domain.model.vehiculo.NuevoVehiculo
import com.tagok.app.domain.model.vehiculo.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface VehiculosUiState {
    data object Idle : VehiculosUiState
    data object Loading : VehiculosUiState
    data class Error(val message: String) : VehiculosUiState
}

class VehiculosViewModel(private val vehiculoRepository: VehiculoRepository) : ViewModel()
{
    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val _uiState = MutableStateFlow<VehiculosUiState>(VehiculosUiState.Idle)
    val uiState: StateFlow<VehiculosUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = VehiculosUiState.Loading
            runCatching { vehiculoRepository.getVehiculos() }
                .onSuccess {
                    _vehiculos.value = it
                    _uiState.value = VehiculosUiState.Idle
                }
                .onFailure {
                    _uiState.value = VehiculosUiState.Error(it.message ?: "Error al cargar vehículos")
                }
        }
    }

    fun agregar(nuevo: NuevoVehiculo) {
        viewModelScope.launch {
            runCatching { vehiculoRepository.insertVehiculo(nuevo) }
                .onSuccess { cargar() }
                .onFailure {
                    _uiState.value = VehiculosUiState.Error(it.message ?: "Error al agregar vehículo")
                }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            runCatching { vehiculoRepository.deleteVehiculo(id) }
                .onSuccess { _vehiculos.value = _vehiculos.value.filter { it.id != id } }
                .onFailure {
                    _uiState.value = VehiculosUiState.Error(it.message ?: "Error al eliminar vehículo")
                }
        }
    }

    fun clearError() { _uiState.value = VehiculosUiState.Idle }

    companion object
    {
        private const val TAG = "VehiculosViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory
        {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val vehiculoApi = VehiculoApi(HttpClientProvider.client)
                val vehiculoRepo = VehiculoRepository(vehiculoApi)

                return VehiculosViewModel(vehiculoRepo) as T
            }
        }
    }
}
