package com.tagok.app.ui.planificar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.tagok.app.ui.map.MapViewModel
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

private val SANTIAGO = Point.fromLngLat(-70.6483, -33.4569)

// Ruta que cruza Vespucio Norte (pórticos P1–P3 con tarifas reales cargadas)
private const val EJEMPLO_ORIGEN = "-33.5100, -70.7000"
private const val EJEMPLO_DESTINO = "-33.4200, -70.8000"

@Composable
fun PlanificarViajeScreen(
    vehiculo: String = "AUTO",
    onBack: () -> Unit = {},
    viewModel: MapViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var origenText by rememberSaveable { mutableStateOf("") }
    var destinoText by rememberSaveable { mutableStateOf("") }
    var origenError by remember { mutableStateOf(false) }
    var destinoError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.setVehiculo(vehiculo) }

    // Limpia el estado al salir para evitar que el RenderThread nativo de Mapbox
    // intente seguir usando una Surface ya destruida.
    DisposableEffect(Unit) {
        onDispose { viewModel.resetMap() }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(SANTIAGO)
            zoom(11.5)
            pitch(0.0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Mapa base
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
        ) {
            if (uiState.routePoints.size >= 2) {
                PolylineAnnotation(points = uiState.routePoints) {
                    lineColor = Blue40
                    lineWidth = 5.0
                    lineOpacity = 0.9
                }
            }

            val cruzadosIds = uiState.porticosCruzados.map { it.id }.toSet()
            uiState.porticos.forEach { portico ->
                PointAnnotation(
                    point = Point.fromLngLat(portico.longitud, portico.latitud),
                ) {
                    if (portico.id in cruzadosIds) iconSize = 1.4
                }
            }
        }

        // Panel superior: inputs de origen y destino
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextSecondary,
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Planificar viaje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(InputBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = vehiculo,
                            style = MaterialTheme.typography.labelSmall,
                            color = Blue40,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                CoordField(
                    value = origenText,
                    onValueChange = { origenText = it; origenError = false },
                    label = "Origen",
                    placeholder = "ej. -33.5892, -70.7152",
                    leadingIcon = Icons.Filled.MyLocation,
                    isError = origenError,
                )

                Spacer(Modifier.height(8.dp))

                CoordField(
                    value = destinoText,
                    onValueChange = { destinoText = it; destinoError = false },
                    label = "Destino",
                    placeholder = "ej. -33.4508, -70.6588",
                    leadingIcon = Icons.Filled.Place,
                    isError = destinoError,
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            origenText = EJEMPLO_ORIGEN
                            destinoText = EJEMPLO_DESTINO
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary),
                    ) {
                        Text(
                            text = "Usar ejemplo",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    Button(
                        onClick = {
                            val origen = parseCoordenada(origenText)
                            val destino = parseCoordenada(destinoText)
                            origenError = origen == null
                            destinoError = destino == null
                            if (origen != null && destino != null) {
                                viewModel.calculateRoute(
                                    lon1 = origen.second, lat1 = origen.first,
                                    lon2 = destino.second, lat2 = destino.first,
                                )
                            }
                        },
                        enabled = !uiState.isLoadingRoute && !uiState.isLoadingTarifa,
                        modifier = Modifier
                            .weight(2f)
                            .height(44.dp),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue40),
                    ) {
                        if (uiState.isLoadingRoute) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Calcular ruta",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }
        }

        // Panel inferior: resultados de la tarifa
        if (uiState.routePoints.isNotEmpty() || uiState.isLoadingTarifa || uiState.tarifaCalculada != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Stats de ruta
                    if (uiState.routePoints.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text("●", color = Blue40, fontSize = 10.sp)
                            Text(
                                text = "${uiState.routePoints.size} segmentos",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                            if (uiState.porticosCruzados.isNotEmpty()) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "· ${uiState.porticosCruzados.size} pórtico(s) en ruta",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Blue40,
                                    fontWeight = FontWeight.Medium,
                                )
                            } else if (!uiState.isLoadingTarifa) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "· sin pórticos en ruta",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                        }
                    }

                    // Sección de tarifa
                    when {
                        uiState.isLoadingTarifa -> {
                            Spacer(Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Blue40,
                                )
                                Text(
                                    text = "Calculando tarifa...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }
                        }

                        uiState.tarifaCalculada != null -> {
                            val tarifa = uiState.tarifaCalculada!!
                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = InputBackground)
                            Spacer(Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(
                                        text = "Tarifa estimada",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextSecondary,
                                    )
                                    Text(
                                        text = "Vehículo: ${tarifa.vehiculo}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                    )
                                }
                                Text(
                                    text = "${"%.0f".format(tarifa.total)} CLP",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Blue40,
                                )
                            }

                            if (tarifa.portico.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                tarifa.portico.take(4).forEach { cruce ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = "${cruce.codigo}  ·  ${cruce.autopista ?: "Autopista"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                        )
                                        Text(
                                            text = "${"%.0f".format(cruce.valor)} CLP",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                                if (tarifa.portico.size > 4) {
                                    Text(
                                        text = "+${tarifa.portico.size - 4} pórticos más",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 2.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 130.dp, start = 16.dp, end = 16.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF1F2937),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}

@Composable
private fun CoordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (isError) MaterialTheme.colorScheme.error else Blue40,
                modifier = Modifier.size(18.dp),
            )
        },
        isError = isError,
        supportingText = if (isError) {
            { Text("Formato: latitud, longitud  (ej. -33.45, -70.65)") }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue40,
            unfocusedBorderColor = InputBackground,
            focusedLabelColor = Blue40,
        ),
    )
}

private fun parseCoordenada(text: String): Pair<Double, Double>? {
    val parts = text.split(",").map { it.trim() }
    if (parts.size != 2) return null
    return try {
        Pair(parts[0].toDouble(), parts[1].toDouble())
    } catch (_: NumberFormatException) {
        null
    }
}
