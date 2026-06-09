package com.tagok.app.di.modules

import com.tagok.app.data.remote.BoletaApi
import com.tagok.app.data.remote.HistoryApi
import com.tagok.app.data.remote.PorticoApi
import com.tagok.app.data.remote.RouteApi
import com.tagok.app.data.remote.TarifaApi
import com.tagok.app.data.remote.VehiculoApi
import com.tagok.app.data.remote.interfaces.IBoletaApi
import com.tagok.app.data.remote.interfaces.IHistoryApi
import com.tagok.app.data.remote.interfaces.IPorticoApi
import com.tagok.app.data.remote.interfaces.IRouteApi
import com.tagok.app.data.remote.interfaces.ITarifaApi
import com.tagok.app.data.remote.interfaces.IVehiculoApi

object ApiModule
{
    val historyApi: IHistoryApi by lazy {
        HistoryApi(NetworkModule.httpClient)
    }

    val boletaApi: IBoletaApi by lazy {
        BoletaApi(NetworkModule.httpClient)
    }

    val porticoApi: IPorticoApi by lazy {
        PorticoApi(NetworkModule.httpClient)
    }

    val routeApi: IRouteApi by lazy {
        RouteApi(NetworkModule.httpClient)
    }

    val tarifaApi: ITarifaApi by lazy {
        TarifaApi(NetworkModule.httpClient)
    }

    val vehiculoApi: IVehiculoApi by lazy {
        VehiculoApi(NetworkModule.httpClient)
    }
}