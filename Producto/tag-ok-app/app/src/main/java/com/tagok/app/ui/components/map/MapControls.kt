package com.tagok.app.ui.components.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

@Composable
fun MapControls(
    mapViewportState: MapViewportState,
    currentZoom: Double,
    hasRoute: Boolean,
    onMyLocation: () -> Unit,
    onFitRoute: () -> Unit,
    modifier: Modifier = Modifier, )
{
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End)
    {
        MapControlButton(
            icon = Icons.Filled.MyLocation,
            contentDescription = "Mi ubicación",
            onClick = onMyLocation)
        MapControlButton(
            icon = Icons.Filled.Add,
            contentDescription = "Acercar",
            onClick = {
                mapViewportState.easeTo(
                    CameraOptions.Builder().zoom((currentZoom + 1.0).coerceAtMost(20.0)).build())
            }
        )
        MapControlButton(
            icon = Icons.Filled.Remove,
            contentDescription = "Alejar",
            onClick = {
                mapViewportState.easeTo(
                    CameraOptions.Builder().zoom((currentZoom - 1.0).coerceAtLeast(1.0)).build())
            }
        )

        if (hasRoute)
        {
            MapControlButton(
                icon = Icons.Filled.ZoomOutMap,
                contentDescription = "Ver ruta completa",
                onClick = onFitRoute)
        }
    }
}

@Composable
private fun MapControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit)
{
    Surface(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .shadow(4.dp, CircleShape),
        shape = CircleShape,
        color = Color.White)
    {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.padding(10.dp),
            tint = Color(0xFF374151),)
    }
}