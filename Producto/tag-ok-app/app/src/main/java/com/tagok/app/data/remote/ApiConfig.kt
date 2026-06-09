package com.tagok.app.data.remote

object ApiConfig
{
    private val GATEWAY_URL: String = "http://10.0.2.2:8080/api"

    val ROUTES_API: String = "${GATEWAY_URL}/routes"
    val TARIFA_API: String = "${GATEWAY_URL}/routes"
    val HISTORY_API: String = "${GATEWAY_URL}/history"
    val BOLETA_API: String = "${GATEWAY_URL}/history"

    val VEHICULOS_API: String = "https://ibafvqmoqeabmziyzifk.supabase.co"
    val VEHICULOS_API_KEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImliYWZ2cW1vcWVhYm16aXl6aWZrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY3ODc5NTIsImV4cCI6MjA5MjM2Mzk1Mn0.ZYYd0xW69sq1CyT6DqsMj23zFSfedrGaed35AhE-GEs"
}