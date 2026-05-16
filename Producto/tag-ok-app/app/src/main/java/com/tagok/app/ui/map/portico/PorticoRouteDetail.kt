package com.tagok.app.ui.map.portico

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Toll
import com.tagok.app.domain.model.routes.Tramo

@Composable
fun PorticoRouteDetail(
    toll: Toll,
    onDismiss: () -> Unit)
{
    val title = when (toll)
    {
        is Portico -> "Pórtico: ${toll.nombre} (${toll.codigo})"
        is Tramo   -> "Tramo: ${toll.nombreEntrada} (${toll.codigoEntrada}) → ${toll.nombreSalida} (${toll.codigoSalida}?"
    }

    BasePorticoBottomSheet(title = title, onDismiss = onDismiss) {
        when (toll)
        {
            is Portico -> {
                PorticoDetailContent(toll)
            }
            is Tramo -> {
                TramoDetailContent(toll)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PorticoDetailContent(
    portico: Portico)
{
    DetalleRow("Autopista", portico.autopista)
    DetalleRow("Tarifa", portico.tarifa)
    DetalleRow("Valor", "$${portico.valor}")
    //DetalleRow("Fecha", portico.fechaHora)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TramoDetailContent(
    tramo: Tramo)
{
    DetalleRow("Autopista", tramo.autopista)
    DetalleRow("Tarifa", tramo.tarifa)
    DetalleRow("Valor", "$${tramo.valor}")
}