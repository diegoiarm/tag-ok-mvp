package com.tagok.app.ui.map.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.ui.theme.Blue40

@Composable
fun RouteLayer(route: Route? = null)
{
    if (route == null)
        return

    val points = remember(route) {
        route.points.map { Point.fromLngLat(it.lon, it.lat) }
    }

    if (points.size < 2)
        return

    PolylineAnnotation(points = points)
    {
        lineColor = Blue40
        lineWidth = 5.0
        lineOpacity = 0.9
    }
}