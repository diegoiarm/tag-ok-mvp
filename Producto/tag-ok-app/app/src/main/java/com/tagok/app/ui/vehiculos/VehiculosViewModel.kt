package com.tagok.app.ui.vehiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.NuevoVehiculo
import com.tagok.app.data.Vehiculo
import com.tagok.app.data.VehiculoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface VehiculosUiState {
    data object Idle : VehiculosUiState
    data object Loading : VehiculosUiState
    data class Error(val message: String) : VehiculosUiState
}

class VehiculosViewModel : ViewModel() {

    private val repository = VehiculoRepository()

    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val _uiState = MutableStateFlow<VehiculosUiState>(VehiculosUiState.Idle)
    val uiState: StateFlow<VehiculosUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = VehiculosUiState.Loading
            runCatching { repository.getVehiculos() }
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
            runCatching { repository.insertVehiculo(nuevo) }
                .onSuccess { cargar() }
                .onFailure {
                    _uiState.value = VehiculosUiState.Error(it.message ?: "Error al agregar vehículo")
                }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            runCatching { repository.deleteVehiculo(id) }
                .onSuccess { _vehiculos.value = _vehiculos.value.filter { it.id != id } }
                .onFailure {
                    _uiState.value = VehiculosUiState.Error(it.message ?: "Error al eliminar vehículo")
                }
        }
    }

    fun clearError() { _uiState.value = VehiculosUiState.Idle }
}
