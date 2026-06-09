package com.tagok.app.di.modules

import com.tagok.app.data.repository.BoletaRepository
import com.tagok.app.data.repository.HistoryRepository
import com.tagok.app.data.repository.PorticoRepository
import com.tagok.app.data.repository.RouteRepository
import com.tagok.app.data.repository.TarifaRepository
import com.tagok.app.data.repository.VehiculoRepository
import com.tagok.app.domain.interfaces.IBoletaRepository
import com.tagok.app.domain.interfaces.IHistoryRepository
import com.tagok.app.domain.interfaces.IPorticoRepository
import com.tagok.app.domain.interfaces.IRouteRepository
import com.tagok.app.domain.interfaces.ITarifaRepository
import com.tagok.app.domain.interfaces.IVehiculoRepository

object RepositoryModule
{
    val boletaRepository: IBoletaRepository by lazy {
        BoletaRepository(ApiModule.boletaApi)
    }

    val historyRepository: IHistoryRepository by lazy {
        HistoryRepository(ApiModule.historyApi)
    }

    val porticoRepository: IPorticoRepository by lazy {
        PorticoRepository(ApiModule.porticoApi)
    }

    val routeRepository: IRouteRepository by lazy {
        RouteRepository(ApiModule.routeApi)
    }

    val tarifaRepository: ITarifaRepository by lazy {
        TarifaRepository(ApiModule.tarifaApi)
    }

    val vehiculoRepository: IVehiculoRepository by lazy {
        VehiculoRepository(ApiModule.vehiculoApi)
    }
}