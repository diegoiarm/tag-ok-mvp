package com.tagok.app.di

import com.tagok.app.di.modules.ServiceModule
import com.tagok.app.di.modules.ViewModelModule
import com.tagok.app.domain.services.interfaces.IBoletaService

object ServiceLocator
{
    val viewModels: ViewModelModule
        get() = ViewModelModule
}