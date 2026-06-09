package com.tagok.app.data.remote

import com.tagok.app.data.dto.route.RouteResponse
import com.tagok.app.data.dto.TarifaCalculada
import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.dto.route.RouteRequest
import com.tagok.app.data.dto.tarifa.TarifaCalculadaResponse
import com.tagok.app.data.remote.interfaces.IRouteApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RouteApi(client: HttpClient) : IRouteApi, ApiClient(client, TAG)
{
    override suspend fun getRoute(request: RouteRequest): RouteResponse = apiCall("Obtener ruta")
    {
        client.post("$BASE_URL/v1/rutas")
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    companion object
    {
        private var BASE_URL = ApiConfig.ROUTES_API
        private const val TAG = "RouteApi"
    }
}