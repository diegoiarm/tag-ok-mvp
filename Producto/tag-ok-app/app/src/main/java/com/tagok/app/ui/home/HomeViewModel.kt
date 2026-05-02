package com.tagok.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.Vehiculo
import com.tagok.app.data.VehiculoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = VehiculoRepository()

    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _loading.value = true
            runCatching { repository.getVehiculos() }
                .onSuccess { _vehiculos.value = it }
                .onFailure { _vehiculos.value = emptyList() }
            _loading.value = false
        }
    }
}
