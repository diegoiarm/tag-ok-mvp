package com.tagok.app.data.remote

import com.tagok.app.data.dto.portico.PorticoResumenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PorticoApi(private val client: HttpClient)
{
    suspend fun getPorticos(): List<PorticoResumenResponse> =
        client.get("${BASE_URL}/porticos").body()

    companion object
    {
        private const val BASE_URL = "http://192.168.1.4:8000"
    }
}