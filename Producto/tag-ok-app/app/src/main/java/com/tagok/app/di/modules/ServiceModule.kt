package com.tagok.app.di.modules

import com.tagok.app.domain.services.BoletaService

object ServiceModule
{
    val boletaService: BoletaService by lazy {
        BoletaService(
            boletaRepository = RepositoryModule.boletaRepository,
            historyRepository = RepositoryModule.historyRepository)
    }
}