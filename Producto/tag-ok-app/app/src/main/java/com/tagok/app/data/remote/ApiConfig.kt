package com.tagok.app.data.remote

object ApiConfig
{
    private const val GATEWAY_URL: String = "http://192.168.1.10:8080/api"

    const val ROUTES_API: String = "$GATEWAY_URL/routes"
    const val TARIFA_API: String = "$GATEWAY_URL/routes"
    const val HISTORY_API: String = "$GATEWAY_URL/history"
    const val BOLETA_API: String = "$GATEWAY_URL/history"
    const val VEHICULOS_API = "$GATEWAY_URL/vehiculos"
    const val PRESUPUESTO_API = "$GATEWAY_URL/presupuesto"
}