package com.tagok.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.data.remote.HttpClientProvider
import com.tagok.app.data.remote.VehiculoApi
import com.tagok.app.data.repository.VehiculoRepository
import com.tagok.app.domain.model.vehiculo.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val vehiculoRepository: VehiculoRepository) : ViewModel()
{
    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init { cargar() }

    fun cargar()
    {
        viewModelScope.launch {
            _loading.value = true
            runCatching { vehiculoRepository.getVehiculos() }
                .onSuccess { _vehiculos.value = it }
                .onFailure { _vehiculos.value = emptyList() }
            _loading.value = false
        }
    }

    companion object
    {
        private const val TAG = "HomeViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory
        {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                val api = VehiculoApi(HttpClientProvider.client)
                val repository = VehiculoRepository(api)
                return HomeViewModel(repository) as T
            }
        }
    }
}
