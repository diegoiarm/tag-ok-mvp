package com.tagok.app.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tagok.app.ui.boleta.BoletaViewModel
import com.tagok.app.ui.historial.HistorialViewModel
import com.tagok.app.ui.map.MapViewModel
import com.tagok.app.ui.map.portico.porticoContainer.PorticosViewModel
import com.tagok.app.ui.planificar.PlanificarViajeViewModel

object ViewModelModule
{
    fun boletaViewModelFactory() = object : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            return BoletaViewModel(ServiceModule.boletaService) as T
        }
    }

    fun planificarViewModelFactory() = object : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            return PlanificarViajeViewModel(ServiceModule.planificarService) as T
        }
    }

    fun porticosViewModelFactory() = object : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            return PorticosViewModel(ServiceModule.porticoService) as T
        }
    }

    fun historialViewModelFactory() = object : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            return HistorialViewModel(ServiceModule.historyService) as T
        }
    }

    fun mapViewModelFactory() = object : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T
        {
            return MapViewModel(
                RepositoryModule.porticoRepository,
                RepositoryModule.tarifaRepository,
                ServiceModule.locationProvider) as T
        }
    }
}