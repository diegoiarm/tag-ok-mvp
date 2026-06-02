package com.tagok.app.data.remote

import io.ktor.client.HttpClient

class HistoryApi(private val client: HttpClient)
{
    fun getHistory()
    {

    }

    companion object
    {
        private var BASE_URL = ApiConfig.HISTORY_API
    }
}