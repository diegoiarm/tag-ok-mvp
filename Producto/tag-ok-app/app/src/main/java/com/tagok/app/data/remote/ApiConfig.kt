package com.tagok.app.data.remote

object ApiConfig
{
    private val GATEWAY_URL: String = "http://192.168.1.4:8080/api"

    val ROUTES_API: String = "${GATEWAY_URL}/routes"
    val HISTORY_API: String = "${GATEWAY_URL}/history"
}