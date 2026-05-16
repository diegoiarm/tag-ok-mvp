package com.tagok.app.ui.map.portico

import PorticoRouteDetail
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mapbox.maps.extension.compose.MapboxMapScope
import com.tagok.app.R
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.model.routes.Tramo
import com.tagok.app.ui.map.vectorToBitmap

@Composable
fun MapboxMapScope.PorticosContainer(
    context: Context,
    porticos: List<PorticoResumen>,
    route: Route?)
{
    val bitmapNormal = remember { vectorToBitmap(context, R.drawable.ic_portico) }
    val bitmapActivo = remember { vectorToBitmap(context, R.drawable.ic_portico_activo) }

    val crossedIds = remember(route?.tolls) {
        route?.tolls?.flatMap { toll ->
            when (toll)
            {
                is Portico -> listOf(toll.porticoId)
                is Tramo   -> listOf(toll.entradaId, toll.salidaId)
            }
        }?.toSet() ?: emptySet()
    }

    var porticoSeleccionado by remember { mutableStateOf<PorticoResumen?>(null) }

    PorticosLayer(
        porticos = porticos,
        crossedIds = crossedIds,
        bitmapNormal = bitmapNormal,
        bitmapActivo = bitmapActivo,
        onPorticoClick = { porticoSeleccionado = it },
    )

    porticoSeleccionado?.let { portico ->
        if (portico.id in crossedIds)
        {
            val toll = route?.tolls?.find { toll ->
                when (toll)
                {
                    is Portico -> toll.porticoId == portico.id
                    is Tramo   -> toll.entradaId == portico.id || toll.salidaId == portico.id
                }
            }
            if (toll != null)
            {
                PorticoRouteDetail(
                    toll = toll,
                    onDismiss = { porticoSeleccionado = null })
            }
            else
            {
                PorticoDetail(
                    porticoId = portico.id,
                    onDismiss = { porticoSeleccionado = null })
            }
        }
        else
        {
            PorticoDetail(
                porticoId = portico.id,
                onDismiss = { porticoSeleccionado = null })
        }
    }
}