package com.tagok.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Protocol

// adb reverse tcp:8000 tcp:8000 → localhost en el dispositivo apunta al PC
private const val BASE_URL = "http://localhost:8000"

private val httpClient = HttpClient(OkHttp) {
    engine {
        config {
            protocols(listOf(Protocol.HTTP_1_1))
        }
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 8_000
        requestTimeoutMillis = 15_000
        socketTimeoutMillis = 15_000
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
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

// --- Tarifa ---

@Serializable
data class PorticoCruzadoRequest(
    val porticoId: Long,
    val horaFechaCruce: String,   // ISO-8601: "2026-04-23T14:30:00"
)

@Serializable
data class TarifaRequest(
    val porticosCruzados: List<PorticoCruzadoRequest>,
    val vehiculo: String,
)

@Serializable
data class Cruce(
    val porticoId: Long,
    val codigo: String,
    val nombre: String? = null,
    val autopista: String? = null,
    val tarifa: String? = null,
    val valor: Double = 0.0,
    // horaFechaCruce omitido — solo necesitamos los valores monetarios
)

@Serializable
data class TarifaCalculada(
    val total: Double,
    val portico: List<Cruce>,   // campo "portico" en el backend
    val vehiculo: String,
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

    suspend fun calculateTarifa(request: TarifaRequest): TarifaCalculada =
        httpClient.post("$BASE_URL/tarifas/calcular") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
