package com.tagok.app.ui.map.portico.porticoContainer

import PorticoRouteDetail
import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.extension.compose.MapboxMapScope
import com.tagok.app.R
import com.tagok.app.domain.model.portico.PorticoResumen
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Route
import com.tagok.app.domain.model.routes.Tramo
import com.tagok.app.domain.vehiculo.TipoVehiculo
import com.tagok.app.ui.map.portico.PorticoDetail
import com.tagok.app.ui.map.portico.PorticosLayer
import com.tagok.app.ui.map.vectorToBitmap

@Composable
fun MapboxMapScope.PorticosContainer(
    context: Context,
    route: Route? = null,
    vehiculo: TipoVehiculo,
    viewModel: PorticosViewModel = viewModel(factory = PorticosViewModel.Factory))
{
    val bitmapNormal = remember { vectorToBitmap(context, R.drawable.ic_portico) }
    val bitmapActivo = remember { vectorToBitmap(context, R.drawable.ic_portico_activo) }

    val porticosUistate by viewModel.uiState.collectAsState()

    val crossedIds = remember(route?.tolls) {
        route?.tolls?.flatMap { toll ->
            when (toll) {
                is Portico -> listOf(toll.porticoId)
                is Tramo   -> listOf(toll.entradaId, toll.salidaId)
            }
        }?.toSet() ?: emptySet()
    }

    var porticoSeleccionado by remember { mutableStateOf<PorticoResumen?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(porticosUistate.error) {
        porticosUistate.error?.let {
            showErrorDialog = true
        }
    }

    if (porticosUistate.porticos.isNotEmpty())
    {
        PorticosLayer(
            porticos = porticosUistate.porticos,
            crossedIds = crossedIds,
            bitmapNormal = bitmapNormal,
            bitmapActivo = bitmapActivo,
            onPorticoClick = { porticoSeleccionado = it })
    }

    porticoSeleccionado?.let { portico ->
        if (portico.id in crossedIds) {
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
                    onDismiss = { porticoSeleccionado = null },
                    vehiculo = vehiculo
                )
            }
        }
        else
        {
            PorticoDetail(
                porticoId = portico.id,
                onDismiss = { porticoSeleccionado = null },
                vehiculo = vehiculo)
        }
    }

    if (showErrorDialog && porticosUistate.error != null)
    {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.clearError()
            },
            title = {
                Text("Error cargando pórticos")
            },
            text = {
                Text(
                    when
                    {
                        porticosUistate.error!!.contains("500") ||
                                porticosUistate.error!!.contains("Internal Server Error") ->
                            "El servicio de pórticos no está disponible en este momento. Por favor, intenta de nuevo más tarde."
                        porticosUistate.error!!.contains("timeout") ||
                                porticosUistate.error!!.contains("Network") ->
                            "Error de conexión. Verifica tu conexión a internet e intenta de nuevo."
                        else -> porticosUistate.error!!
                    })
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showErrorDialog = false
                        viewModel.clearError()
                    })
                {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showErrorDialog = false
                        viewModel.clearError()
                        viewModel.cargarPorticos()
                    })
                {
                    Text("Reintentar")
                }
            })
    }
}