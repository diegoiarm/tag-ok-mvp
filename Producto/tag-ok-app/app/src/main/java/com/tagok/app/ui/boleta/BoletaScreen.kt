package com.tagok.app.ui.boleta

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
import com.tagok.app.ui.common.ScreenLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoletaScreen(
    onBack: () -> Unit = {},
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
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Sección de selección de patente
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Vehículo",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            PatenteSelector(
                                patentes = uiState.patentes,
                                selected = uiState.patenteSeleccionada,
                                onPatenteSelected = { viewModel.setPatente(it) }
                            )
                        }
                    }

                    // Sección de fechas
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Período",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            DateRangeSelector(
                                fechaDesde = uiState.fechaDesde,
                                fechaHasta = uiState.fechaHasta,
                                onDesdeChanged = { viewModel.setFechaDesde(it) },
                                onHastaChanged = { viewModel.setFechaHasta(it) }
                            )
                        }
                    }

                    // Sección de autopistas
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Autopistas (opcional)",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            AutopistaMultiSelect(
                                autopistas = uiState.autopistas,
                                selected = uiState.autopistasSeleccionadas,
                                onToggle = { viewModel.toggleAutopista(it) }
                            )
                        }
                    }

                    // Botón generar
                    Button(
                        onClick = { viewModel.generarBoleta() },
                        enabled = !uiState.isLoading && uiState.patenteSeleccionada.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Generar Boleta",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Mostrar resultado
                    uiState.boleta?.let { boleta ->
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // Tarjeta de total
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total a pagar",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "$${String.format("%.2f", boleta.total)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Lista de items
                        Text(
                            text = "Detalle de transacciones",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(boleta.items) { item ->
                                BoletaItemRow(item = item)
                            }
                        }
                    }

                    // Espacio extra al final para mejor scroll
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Mostrar error como diálogo
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Text(
                    text = "Error",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearError() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
fun BoletaItemRow(item: BoletaItem) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 6.dp else 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isHovered) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Clave en negrita (nombre)
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Autopista
                Text(
                    text = item.autopista,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Información secundaria
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.horaCruce.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    // Separador visual
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .defaultMinSize(minWidth = 3.dp, minHeight = 3.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(50)
                        ) {}
                    }

                    Text(
                        text = item.tipoTarifa,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Monto mejorado
            Surface(
                color = if (isHovered) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "$${String.format("%.2f", item.valor)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHovered) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}