package com.tagok.app.di.modules

import com.tagok.app.domain.services.BoletaService
import com.tagok.app.domain.services.HistoryService
import com.tagok.app.domain.services.PlanificarService
import com.tagok.app.domain.services.PorticoService
import com.tagok.app.domain.services.interfaces.IBoletaService
import com.tagok.app.domain.services.interfaces.IHistoryService
import com.tagok.app.domain.services.interfaces.IPlanificarService
import com.tagok.app.domain.services.interfaces.IPorticoService

object ServiceModule
{
    val boletaService: BoletaService by lazy {
        BoletaService(
            boletaRepository = RepositoryModule.boletaRepository,
            historyRepository = RepositoryModule.historyRepository)
    }

    val planificarService: IPlanificarService by lazy {
        PlanificarService(routeRepository = RepositoryModule.routeRepository)
    }

    val porticoService: IPorticoService by lazy {
        PorticoService(porticoRepository = RepositoryModule.porticoRepository)
    }

    val historyService: IHistoryService by lazy {
        HistoryService(historyRepository = RepositoryModule.historyRepository)
    }
}