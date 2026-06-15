package com.tagok.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tagok.app.di.modules.RepositoryModule
import com.tagok.app.di.modules.ServiceModule
import com.tagok.app.domain.interfaces.INotificacionRepository
import com.tagok.app.domain.interfaces.IVehiculoRepository
import com.tagok.app.domain.model.notificacion.NuevaNotificacion
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.domain.services.AlertaService
import com.tagok.app.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val vehiculoRepository: IVehiculoRepository,
    private val alertaService: AlertaService,
    private val notificacionRepository: INotificacionRepository) : ViewModel()
{
    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    // Alertas recién generadas que la UI debe mostrar como notificación local.
    private val _alertasPendientes = MutableStateFlow<List<NuevaNotificacion>>(emptyList())
    val alertasPendientes: StateFlow<List<NuevaNotificacion>> = _alertasPendientes.asStateFlow()

    init { cargar() }

    fun cargar()
    {
        viewModelScope.launch {
            _loading.value = true
            runCatching { vehiculoRepository.getVehiculos() }
                .onSuccess { _vehiculos.value = it }
                .onFailure { _vehiculos.value = emptyList() }
            _loading.value = false

            revisarAlertas()
        }
    }

    private suspend fun revisarAlertas()
    {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return
        val nuevas = runCatching { alertaService.revisarYNotificar(userId) }.getOrDefault(emptyList())
        if (nuevas.isNotEmpty()) _alertasPendientes.value = nuevas
        refrescarNoLeidas()
    }

    private suspend fun refrescarNoLeidas()
    {
        val count = runCatching { notificacionRepository.getAll().count { !it.leida } }.getOrDefault(0)
        _unreadCount.value = count
    }

    /** La UI llama esto tras mostrar las notificaciones locales para no repetirlas. */
    fun consumirAlertas() { _alertasPendientes.value = emptyList() }

    companion object
    {
        private const val TAG = "HomeViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory
        {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T
            {
                return HomeViewModel(
                    RepositoryModule.vehiculoRepository,
                    ServiceModule.alertaService,
                    RepositoryModule.notificacionRepository) as T
            }
        }
    }
}
