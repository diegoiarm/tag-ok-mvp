package com.tagok.app.ui.map.portico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.data.repository.PorticoRepository
import com.tagok.app.domain.model.portico.PorticoType
import com.tagok.app.domain.model.portico.ReglaTarifaria
import com.tagok.app.domain.model.portico.TollType
import com.tagok.app.domain.model.portico.TramoType
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PorticoDetail(
    porticoId: Long,
    onDismiss: () -> Unit,
    viewModel: PorticoDetailViewModel = viewModel(factory = PorticoDetailViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(porticoId) { viewModel.load(porticoId) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
        {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Detalle del pórtico",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold)

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp))
                {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))

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
                    color = TextSecondary,)

                uiState.detalle != null -> PorticoDetalleContent(detalle = uiState.detalle!!)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun PorticoDetalleContent(detalle: TollType)
{
    when (detalle)
    {
        is PorticoType -> PorticoTypeContent(detalle)
        is TramoType   -> TramoTypeContent(detalle)
    }
}

// ── PorticoType ──────────────────────────────────────────────────────────────

@Composable
private fun PorticoTypeContent(detalle: PorticoType)
{
    Column(verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        DetalleRow("Nombre",    detalle.nombre)
        DetalleRow("Autopista", detalle.autopista)
        DetalleRow("Sentido",   detalle.sentido)
        DetalleRow("Código",    detalle.codigo)

        Spacer(Modifier.height(4.dp))
        ReglasTarifariasCard(reglas = detalle.reglas, calendario = detalle.calendario)
    }
}

// ── TramoType ────────────────────────────────────────────────────────────────

@Composable
private fun TramoTypeContent(detalle: TramoType)
{
    Column(verticalArrangement = Arrangement.spacedBy(10.dp))
    {
        DetalleRow("Entrada", detalle.entrada)
        DetalleRow("Salida",  detalle.salida)

        Spacer(Modifier.height(4.dp))
        ReglasTarifariasCard(reglas = detalle.reglas, calendario = detalle.calendario)
    }
}


@Composable
private fun DetalleRow(label: String, value: String)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween)
    {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}