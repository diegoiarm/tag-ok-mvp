package com.tagok.app.ui.boleta

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.domain.model.boleta.BoletaItem
import com.tagok.app.ui.boleta.components.AutopistaMultiSelect
import com.tagok.app.ui.boleta.components.DateRangeSelector
import com.tagok.app.ui.boleta.components.PatenteSelector
import com.tagok.app.ui.common.ErrorContent
import com.tagok.app.ui.common.LoadingState
import com.tagok.app.ui.common.ScreenLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoletaScreen(
    onBack: () -> Unit = {},
    onVerificarFactura: (
        patente: String,
        fechaDesde: kotlinx.datetime.LocalDate,
        fechaHasta: kotlinx.datetime.LocalDate,
        autopistas: List<String>) -> Unit = { _, _, _, _ -> },
    viewModel: BoletaViewModel = viewModel(factory = BoletaViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenLifecycle(viewModel = viewModel)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generar Boleta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                })
        })
    { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding))
        {
            when
            {
                uiState.isLoading && uiState.patentes.isEmpty() -> {
                    LoadingState(message = "Cargando datos...")
                }

                uiState.error != null && uiState.patentes.isEmpty() -> {
                    ErrorContent(
                        message = uiState.error ?: "Error desconocido",
                        onRetry = { viewModel.refreshData() },
                        onDismiss = { viewModel.clearError() })
                }

                else -> {
                    BoletaContent(
                        uiState = uiState,
                        onPatenteSelected = viewModel::setPatente,
                        onFechaDesdeChanged = viewModel::setFechaDesde,
                        onFechaHastaChanged = viewModel::setFechaHasta,
                        onToggleAutopista = viewModel::toggleAutopista,
                        onGenerarBoleta = viewModel::generarBoleta,
                        onVerificarFactura = onVerificarFactura)
                }
            }

            if (uiState.isLoading && uiState.patentes.isNotEmpty())
            {
                LoadingOverlay()
            }
        }
    }

    if (uiState.error != null && uiState.patentes.isNotEmpty())
    {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Text(
                    text = "Error",
                    fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = uiState.error ?: "",
                    modifier = Modifier.padding(vertical = 8.dp))
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearError() },
                    modifier = Modifier.padding(8.dp))
                {
                    Text("Entendido")
                }
            })
    }
}

@Composable
private fun LoadingOverlay()
{
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp))
        {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp))
            {
                CircularProgressIndicator()
                Text(
                    text = "Generando boleta...",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun BoletaContent(
    uiState: BoletaUiState,
    onPatenteSelected: (String) -> Unit,
    onFechaDesdeChanged: (kotlinx.datetime.LocalDate) -> Unit,
    onFechaHastaChanged: (kotlinx.datetime.LocalDate) -> Unit,
    onToggleAutopista: (String) -> Unit,
    onGenerarBoleta: () -> Unit,
    onVerificarFactura: (
        patente: String,
        fechaDesde: kotlinx.datetime.LocalDate,
        fechaHasta: kotlinx.datetime.LocalDate,
        autopistas: List<String>) -> Unit = { _, _, _, _ -> })
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp))
    {
        FormSection(title = "Vehículo")
        {
            PatenteSelector(
                patentes = uiState.patentes,
                selected = uiState.patenteSeleccionada,
                onPatenteSelected = onPatenteSelected)
        }

        uiState.boleta?.let { boleta ->
            BoletaResult(boleta = boleta)

            // Atajo a la verificación con IA: compara esta boleta contra la
            // factura (PDF o foto) que entrega la concesionaria
            FilledTonalButton(
                onClick = {
                    onVerificarFactura(
                        uiState.patenteSeleccionada,
                        uiState.fechaDesde,
                        uiState.fechaHasta,
                        uiState.autopistasSeleccionadas)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium)
            {
                Icon(
                    Icons.Default.DocumentScanner,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verificar factura con IA",
                    style = MaterialTheme.typography.titleSmall)
            }
        }

        FormSection(title = "Período")
        {
            DateRangeSelector(
                fechaDesde = uiState.fechaDesde,
                fechaHasta = uiState.fechaHasta,
                onDesdeChanged = onFechaDesdeChanged,
                onHastaChanged = onFechaHastaChanged)
        }

        FormSection(title = "Autopistas (opcional)")
        {
            AutopistaMultiSelect(
                autopistas = uiState.autopistas,
                selected = uiState.autopistasSeleccionadas,
                onToggle = onToggleAutopista)
        }

        Button(
            onClick = onGenerarBoleta,
            enabled = !uiState.isLoading && uiState.patenteSeleccionada.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium)
        {
            Text(
                text = if (uiState.boleta != null) "Regenerar Boleta" else "Generar Boleta",
                style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BoletaResult(boleta: com.tagok.app.domain.model.boleta.Boleta)
{
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(boleta) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }))
    {
        Column(
            modifier = Modifier.fillMaxWidth())
        {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                thickness = 2.dp)

            Text(
                text = "📄 Boleta Generada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp))
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Column {
                        Text(
                            text = "Total a pagar",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        Text(
                            text = "${boleta.items.size} transacciones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f))
                    }
                    Text(
                        text = "$${String.format("%.2f", boleta.total)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Detalle de transacciones",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp))
            {
                items(boleta.items) { item ->
                    BoletaItemRow(item = item)
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                thickness = 2.dp
            )
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable () -> Unit)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)))
    {
        Column(modifier = Modifier.padding(16.dp))
        {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp))
            content()
        }
    }
}

@Composable
fun BoletaItemRow(item: BoletaItem)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 6.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHovered)
            {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            }
            else
            {
                MaterialTheme.colorScheme.surface
            }),
        shape = RoundedCornerShape(12.dp))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            Column(
                modifier = Modifier.weight(1f))
            {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.autopista,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Text(
                        text = item.horaCruce.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)

                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .defaultMinSize(minWidth = 3.dp, minHeight = 3.dp))
                    {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(50)) {}
                    }

                    Text(
                        text = item.tipoTarifa,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Surface(
                color = if (isHovered)
                {
                    MaterialTheme.colorScheme.primary
                }
                else
                {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                shape = RoundedCornerShape(8.dp))
            {
                Text(
                    text = "$${String.format("%.2f", item.valor)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHovered) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
            }
        }
    }
}