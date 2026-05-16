package com.tagok.app.ui.map

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMapScope
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.tagok.app.domain.model.portico.PorticoResumen

@Composable
fun MapboxMapScope.PorticosLayer(
    porticos: List<PorticoResumen>,
    crossedIds: Set<Long>,
    bitmapNormal: Bitmap?,
    bitmapActivo: Bitmap?)
{
    porticos.forEach { portico ->
        val activo = portico.id in crossedIds
        val bitmap = if (activo) bitmapActivo else bitmapNormal
        if (bitmap != null)
        {
            PointAnnotation(point = Point.fromLngLat(portico.longitud, portico.latitud))
            {
                iconImage = IconImage(bitmap)
                iconSize = if (activo) 1.5 else 0.5
            }
        }
    }
}