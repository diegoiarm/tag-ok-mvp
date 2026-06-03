package com.tagok.app.ui.map

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.tagok.app.MainActivity
import com.tagok.app.R
import com.tagok.app.domain.model.tarifa.Cruce
import com.tagok.app.domain.model.tarifa.TarifaCalculada

object NotificationUtils
{
    private const val CHANNEL_CRUCES = "channel_cruces"
    private const val NOTIFICATION_ID = 1001
    private const val MAX_CRUCES_VISIBLES = 3

    // Acumulador de cruces
    private val crucesAcumulados = mutableListOf<Cruce>()
    private var totalAcumulado = 0.0
    private var cantidadTotal = 0

    fun createNotificationChannel(context: Context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                CHANNEL_CRUCES,
                "Cruces de Pórticos",
                NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notificaciones de cruces realizados"
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun agregarCruce(
        context: Context,
        cruce: Cruce,
        valorTotal: Double)
    {
        crucesAcumulados.add(cruce)
        totalAcumulado += valorTotal
        cantidadTotal++

        actualizarNotificacion(context)
    }

    fun agregarMultiplesCruces(
        context: Context,
        tarifaCalculada: TarifaCalculada)
    {
        crucesAcumulados.addAll(tarifaCalculada.cruces)
        totalAcumulado += tarifaCalculada.total
        cantidadTotal += tarifaCalculada.cruces.size

        actualizarNotificacion(context)
    }

    fun limpiarAcumulador()
    {
        crucesAcumulados.clear()
        totalAcumulado = 0.0
        cantidadTotal = 0
    }

    private fun actualizarNotificacion(context: Context)
    {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val titulo = when
        {
            cantidadTotal == 1 -> "¡Cruzaste un pórtico!"
            cantidadTotal <= 5 -> "¡Has cruzado $cantidadTotal pórticos!"
            else -> "¡$cantidadTotal cruces acumulados!"
        }

        val contenido = when
        {
            cantidadTotal == 1 -> {
                val c = crucesAcumulados.first()
                "${c.nombre}\nTarifa: ${c.tipoTarifa} • ${formatPeso(c.valor)} - ${c.tipoTarifa}"
            }
            cantidadTotal <= MAX_CRUCES_VISIBLES -> {
                crucesAcumulados.joinToString("\n") { cruce ->
                    "• ${cruce.nombre} - ${formatPeso(cruce.valor)} - ${cruce.tipoTarifa}"
                }
            }
            else -> {
                val ultimos = crucesAcumulados.takeLast(MAX_CRUCES_VISIBLES)
                val resto = cantidadTotal - MAX_CRUCES_VISIBLES

                ultimos.joinToString("\n") { cruce ->
                    "• ${cruce.nombre} - ${formatPeso(cruce.valor)} - ${cruce.tipoTarifa}"
                } + "\ny $resto cruces más..."
            }
        }

        val subtexto = "Total acumulado: ${formatPeso(totalAcumulado)}"

        val notification = NotificationCompat.Builder(context, CHANNEL_CRUCES)
            .setSmallIcon(R.drawable.ic_portico)
            .setContentTitle(titulo)
            .setContentText(contenido.lines().first())
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(contenido)
                    .setSummaryText(subtexto))
            .setSubText(subtexto)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setNumber(cantidadTotal)
            .setOnlyAlertOnce(false)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatPeso(amount: Double): String
    {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "CL"))
        return format.format(amount)
    }
}