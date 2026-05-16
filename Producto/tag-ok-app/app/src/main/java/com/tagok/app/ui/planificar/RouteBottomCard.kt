package com.tagok.app.ui.planificar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tagok.app.data.GeocodeSuggestion
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.ui.components.map.DireccionField
import com.tagok.app.ui.components.map.RouteResult
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun RouteBottomCard(
    vehiculo: String,
    isMinimized: Boolean,
    onToggleMinimized: () -> Unit,
    onBack: () -> Unit,
    // Origen
    origenText: String,
    onOrigenChange: (String) -> Unit,
    origenSugerencias: List<GeocodeSuggestion>,
    onOrigenSugerenciaClick: (GeocodeSuggestion) -> Unit,
    origenSeleccionado: GeocodeSuggestion?,
    buscandoOrigen: Boolean,
    // Destino
    destinoText: String,
    onDestinoChange: (String) -> Unit,
    destinoSugerencias: List<GeocodeSuggestion>,
    onDestinoSugerenciaClick: (GeocodeSuggestion) -> Unit,
    destinoSeleccionado: GeocodeSuggestion?,
    buscandoDestino: Boolean,
    // Ruta
    route: Route?,
    hasRoutePoints: Boolean,
    isLoadingRoute: Boolean,
    onCalcular: () -> Unit,
    onUsarEjemplo: () -> Unit,
    onFlyToPortico: (lat: Double, lon: Double) -> Unit,
    modifier: Modifier = Modifier,)
{
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp))
    {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
        {
            // ── Header ──────────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp))
                {
                    Icon(Icons.Filled.ArrowBackIosNew, "Volver", tint = TextSecondary)
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Planificar viaje",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(InputBackground)
                        .padding(horizontal = 10.dp, vertical = 4.dp))
                {
                    Text(
                        text = vehiculo,
                        style = MaterialTheme.typography.labelSmall,
                        color = Blue40,
                        fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onToggleMinimized)
                {
                    Icon(
                        imageVector = if (isMinimized) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isMinimized) "Expandir" else "Minimizar",
                        tint = TextSecondary)
                }
            }

            // ── Expanded content ─────────────────────────────────────────────────
            AnimatedVisibility(visible = !isMinimized)
            {
                Column {
                    Spacer(Modifier.height(10.dp))

                    DireccionField(
                        value = origenText,
                        onValueChange = onOrigenChange,
                        sugerencias = origenSugerencias,
                        onSugerenciaClick = onOrigenSugerenciaClick,
                        label = "Origen",
                        leadingIcon = Icons.Filled.MyLocation,
                        cargando = buscandoOrigen,
                    )

                    Spacer(Modifier.height(8.dp))

                    DireccionField(
                        value = destinoText,
                        onValueChange = onDestinoChange,
                        sugerencias = destinoSugerencias,
                        onSugerenciaClick = onDestinoSugerenciaClick,
                        label = "Destino",
                        leadingIcon = Icons.Filled.Place,
                        cargando = buscandoDestino)

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        TextButton(
                            onClick = onUsarEjemplo,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary))
                        {
                            Text("Usar ejemplo", style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = onCalcular,
                            enabled = origenSeleccionado != null && destinoSeleccionado != null && !isLoadingRoute,
                            modifier = Modifier
                                .weight(2f)
                                .height(44.dp),
                            shape = RoundedCornerShape(13.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue40))
                        {
                            if (isLoadingRoute)
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp)
                            else
                                Text(
                                    text = "Calcular ruta",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp)
                        }
                    }

                    if (hasRoutePoints)
                    {
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = InputBackground)
                        Spacer(Modifier.height(8.dp))
                        RouteResult(
                            vehiculo = vehiculo,
                            route = route,
                            onFlyToPortico = onFlyToPortico,
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isMinimized)
            {
                if (origenSeleccionado != null && destinoSeleccionado != null)
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween)
                    {
                        Column(modifier = Modifier.weight(1f))
                        {
                            Text(
                                text = "${origenSeleccionado.placeName} → ${destinoSeleccionado.placeName}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = TextSecondary)

                            Text(
                                text = "${"%.0f".format(route?.totalCost)} CLP",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Blue40)
                        }

                        Button(
                            onClick = onCalcular,
                            enabled = !isLoadingRoute,
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue40),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        )
                        {
                            if (isLoadingRoute)
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp)
                            else
                                Icon(
                                    Icons.Filled.Refresh,
                                    contentDescription = "Recalcular",
                                    modifier = Modifier.size(16.dp))
                        }
                    }
                }
                else
                    Text(
                        text = "Completa origen y destino para calcular",
                        style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}