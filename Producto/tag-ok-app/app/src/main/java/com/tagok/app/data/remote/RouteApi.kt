package com.tagok.app.data.remote

import com.tagok.app.data.dto.PorticoResumen
import com.tagok.app.data.dto.RouteResponse
import com.tagok.app.data.dto.TarifaCalculada
import com.tagok.app.data.dto.TarifaRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RouteApi(
    private val client: HttpClient)
{
    suspend fun getRoute(
        lon1: Double,
        lat1: Double,
        lon2: Double,
        lat2: Double): RouteResponse =
        client.get("$BASE_URL/api/routes")
        {
            parameter("lon1", lon1)
            parameter("lat1", lat1)
            parameter("lon2", lon2)
            parameter("lat2", lat2)
        }.body()

    suspend fun getPorticos(): List<PorticoResumen> =
        client.get("$BASE_URL/porticos").body()



    suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculada =
        client.post("$BASE_URL/tarifas/calcular")
        {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    companion object
    {
        private const val BASE_URL = "http://192.168.1.4:8000"
    }
}