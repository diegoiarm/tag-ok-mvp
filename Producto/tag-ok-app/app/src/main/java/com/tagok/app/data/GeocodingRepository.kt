package com.tagok.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.net.URLEncoder

private const val NOMINATIM_BASE = "https://nominatim.openstreetmap.org/search"

private val nominatimClient = HttpClient(OkHttp) {
    install(HttpTimeout) {
        requestTimeoutMillis = 10_000
        connectTimeoutMillis = 8_000
    }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

object GeocodingRepository {

    suspend fun buscar(query: String): List<GeocodeSuggestion> {
        if (query.length < 3) return emptyList()
        val encoded = URLEncoder.encode(query.trim(), "UTF-8")

        val url = "$NOMINATIM_BASE" +
            "?q=$encoded" +
            "&format=json" +
            "&countrycodes=cl" +
            "&limit=10" +
            "&viewbox=-71.5,-32.8,-70.0,-34.3" +
            "&bounded=1" +
            "&accept-language=es"

        val results: List<NominatimResult> = nominatimClient.get(url) {
            // Nominatim requiere User-Agent identificable por política de uso
            header(HttpHeaders.UserAgent, "TagOkApp/1.0 Android")
        }.body()

        return results.map { result ->
            GeocodeSuggestion(
                placeName = result.displayName.limpiar(),
                lon = result.lon.toDouble(),
                lat = result.lat.toDouble(),
            )
        }
    }

    private fun String.limpiar(): String {
        var s = this
        listOf(
            ", Chile",
            ", Región Metropolitana de Santiago",
            ", Provincia de Santiago",
        ).forEach { s = s.removeSuffix(it) }
        return s
    }
}
