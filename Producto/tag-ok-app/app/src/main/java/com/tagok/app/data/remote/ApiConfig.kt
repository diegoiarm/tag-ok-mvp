package com.tagok.app.data.remote

object ApiConfig
{
    // Emulador Android: 10.0.2.2 es el alias del host (loopback de tu PC), evita el firewall.
    // Para un dispositivo fisico en la misma WiFi, usa la IP LAN del PC (p.ej. 192.168.1.10)
    // y abre el puerto 8080 en el Firewall de Windows.
    private const val GATEWAY_URL: String = "http://10.0.2.2:8080/api"

    const val ROUTES_API: String = "$GATEWAY_URL/routes"
    const val TARIFA_API: String = "$GATEWAY_URL/routes"
    const val HISTORY_API: String = "$GATEWAY_URL/history"
    const val BOLETA_API: String = "$GATEWAY_URL/history"
    const val VEHICULOS_API = "$GATEWAY_URL/vehiculos"
    const val PRESUPUESTO_API = "$GATEWAY_URL/presupuesto"
    const val NOTIFICACION_API = "$GATEWAY_URL/notificaciones"
}