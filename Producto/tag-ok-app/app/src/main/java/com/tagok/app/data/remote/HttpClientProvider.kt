// data/remote/HttpClientProvider.kt
package com.tagok.app.data.remote

import android.util.Log
import com.tagok.app.data.auth.AuthTokenProvider
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Protocol

object HttpClientProvider
{
    private const val TAG = "HttpClientProvider"

    val client = HttpClient(OkHttp)
    {
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

        install(AuthPlugin)

        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }
}

object AuthPlugin : HttpClientPlugin<Unit, AuthPlugin>
{

    override val key: AttributeKey<AuthPlugin> = AttributeKey("AuthPlugin")

    override fun prepare(block: Unit.() -> Unit): AuthPlugin
    {
        return this
    }

    override fun install(plugin: AuthPlugin, scope: HttpClient)
    {
        scope.requestPipeline.intercept(HttpRequestPipeline.State)
        {
            val token = runBlocking { AuthTokenProvider.getAccessToken() }
            Log.d("AuthPlugin", "🔑 Token: ${token?.take(30) ?: "NULL"}")

            if (!token.isNullOrBlank())
            {
                context.headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                Log.d("AuthPlugin", "Bearer agregado a ${context.url}")
            }
            else
            {
                Log.e("AuthPlugin", "Token NULL - ${context.url}")
            }
        }
    }
}
