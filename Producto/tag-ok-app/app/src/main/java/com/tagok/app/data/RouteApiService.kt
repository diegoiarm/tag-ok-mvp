package com.tagok.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Emulador → 10.0.2.2 mapea a localhost de la máquina host
// Dispositivo físico → reemplazar por la IP local de tu máquina (ej. 192.168.x.x)
private const val BASE_URL = "http://10.0.2.2:8000"

private val httpClient = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

@Serializable
data class RouteResponse(
    val segments: List<RouteSegment>,
    val totalCost: Double,
)

@Serializable
data class RouteSegment(
    val seq: Int = 0,
    val edgeId: Long = 0,
    val node: Long = 0,
    val cost: Double = 0.0,
    val aggCost: Double = 0.0,
    val name: String? = null,
    val geometry: String = "",   // JSON string: {"type":"LineString","coordinates":[[lon,lat],...]}
)

@Serializable
data class PorticoResumen(
    val id: Long,
    val codigo: String,
    val sentido: String? = null,
    val latitud: Double,
    val longitud: Double,
)

object RouteApiService {

    suspend fun getRoute(lon1: Double, lat1: Double, lon2: Double, lat2: Double): RouteResponse =
        httpClient.get("$BASE_URL/api/routes") {
            parameter("lon1", lon1)
            parameter("lat1", lat1)
            parameter("lon2", lon2)
            parameter("lat2", lat2)
        }.body()

    suspend fun getPorticos(): List<PorticoResumen> =
        httpClient.get("$BASE_URL/porticos").body()
}
