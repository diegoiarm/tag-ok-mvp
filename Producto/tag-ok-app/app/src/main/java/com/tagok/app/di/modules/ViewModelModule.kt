package com.tagok.app.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tagok.app.ui.boleta.BoletaViewModel

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
}