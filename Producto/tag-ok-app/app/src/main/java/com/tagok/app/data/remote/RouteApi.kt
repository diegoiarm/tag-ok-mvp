package com.tagok.app.data.remote

import com.tagok.app.data.dto.route.RouteResponse
import com.tagok.app.data.dto.TarifaCalculada
import com.tagok.app.data.dto.TarifaRequest
import com.tagok.app.data.dto.route.RouteRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RouteApi(private val client: HttpClient)
{
    suspend fun getRoute(request: RouteRequest): RouteResponse =
        client.post("$BASE_URL/v1/rutas")
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculada =
        client.post("$BASE_URL/v1/tarifas")
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    companion object
    {
        private var BASE_URL = ApiConfig.ROUTES_API
    }
}