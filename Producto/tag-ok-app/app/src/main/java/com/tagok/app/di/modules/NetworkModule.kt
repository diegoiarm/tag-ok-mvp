package com.tagok.app.di.modules

import com.tagok.app.data.remote.HttpClientProvider
import io.ktor.client.HttpClient

object NetworkModule
{
    val httpClient: HttpClient by lazy {
        HttpClientProvider.client
    }
}