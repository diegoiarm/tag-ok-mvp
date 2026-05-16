package com.tagok.app.ui.map

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mapbox.maps.extension.compose.MapboxMapScope
import com.tagok.app.R
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.model.routes.Tramo

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

    PorticosLayer(
        porticos = porticos,
        crossedIds = crossedIds,
        bitmapNormal = bitmapNormal,
        bitmapActivo = bitmapActivo,
    )
}