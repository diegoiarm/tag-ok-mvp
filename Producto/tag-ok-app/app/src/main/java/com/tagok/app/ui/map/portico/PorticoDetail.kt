package com.tagok.app.ui.map.portico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.domain.model.portico.PorticoTramoType
import com.tagok.app.domain.model.portico.PorticoType
import com.tagok.app.domain.model.portico.TollType
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PorticoDetail(
    porticoId: Long,
    onDismiss: () -> Unit,
    viewModel: PorticoDetailViewModel = viewModel(factory = PorticoDetailViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(porticoId) { viewModel.load(porticoId) }

    BasePorticoBottomSheet(title = "Detalle del pórtico", onDismiss = onDismiss)
    {
        when
        {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center)
            {
                CircularProgressIndicator(color = Blue40, modifier = Modifier.size(32.dp))
            }

            uiState.error != null -> Text(
                text = "No se pudo cargar la información.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            uiState.detalle != null -> PorticoDetalleContent(
                detalle = uiState.detalle!!,
                tipoTarifa = uiState.tipoTarifaActual!!,
                tipoAuto = uiState.tipoVehiculo!!)
        }
    }
}

@Composable
fun PorticoDetalleContent(detalle: TollType, tipoTarifa: String, tipoAuto: String) {
    when (detalle) {
        is PorticoType -> PorticoTypeContent(detalle, tipoTarifa, tipoAuto)
        is PorticoTramoType -> PorticoTramoContent(detalle, tipoTarifa, tipoAuto)
    }
}

// ── PorticoType ──────────────────────────────────────────────────────────────

@Composable
private fun PorticoTypeContent(detalle: PorticoType, tipoTarifa: String, tipoAuto: String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        DetalleRow("Nombre", detalle.nombre)
        DetalleRow("Autopista", detalle.autopista)
        DetalleRow("Sentido", detalle.sentido)
        DetalleRow("Código", detalle.codigo)

        Spacer(Modifier.height(4.dp))
        ReglasTarifariasCard(
            reglas = detalle.reglas,
            calendario = detalle.calendario,
            tipoTarifa,
            tipoAuto)
    }
}

// ── TramoType ────────────────────────────────────────────────────────────────

@Composable
private fun PorticoTramoContent(
    detalle: PorticoTramoType,
    tipoTarifa: String,
    tipoAuto: String
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        DetalleRow("Nombre", detalle.nombre)
        DetalleRow("Autopista", detalle.autopista)
        DetalleRow("Código", detalle.codigo)

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Tramos",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary)

        detalle.tramos.forEach { tramo ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = InputBackground))
            {
                Column(modifier = Modifier.padding(12.dp))
                {
                    DetalleRow("Entrada", "${tramo.nombreEntrada} (${tramo.entrada})")
                    DetalleRow("Salida", "${tramo.nombreSalida} (${tramo.salida})")

                    Spacer(Modifier.height(6.dp))

                    ReglasTarifariasCard(
                        reglas = tramo.reglas,
                        calendario = tramo.calendario,
                        tipoTarifaActual = tipoTarifa,
                        tipoAuto = tipoAuto
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}