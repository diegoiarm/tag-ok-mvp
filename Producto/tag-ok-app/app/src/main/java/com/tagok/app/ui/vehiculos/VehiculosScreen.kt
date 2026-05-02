package com.tagok.app.ui.vehiculos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.data.NuevoVehiculo
import com.tagok.app.data.Vehiculo
import com.tagok.app.supabase
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary
import io.github.jan.supabase.auth.auth

private val CardDark = Color(0xFF1F2937)

private val tipoOpciones = listOf(
    "AUTO" to "Automóvil",
    "MOTO" to "Motocicleta",
    "CAMIONETA" to "Camioneta",
    "BUS" to "Bus",
    "CAMION" to "Camión",
    "CAMION_REMOLQUE" to "Camión con remolque",
)

private val categoriaOpciones = listOf(
    1 to "Categoría 1 — Motocicletas",
    2 to "Categoría 2 — Autos y camionetas",
    3 to "Categoría 3 — Buses",
    4 to "Categoría 4 — Camiones 2 ejes",
    5 to "Categoría 5 — Camiones 3 ejes",
    6 to "Categoría 6 — Camiones 4 o más ejes",
)

private fun tipoIcon(tipo: String): ImageVector = when (tipo) {
    "MOTO" -> Icons.Filled.TwoWheeler
    "BUS" -> Icons.Filled.DirectionsBus
    "CAMION", "CAMION_REMOLQUE" -> Icons.Filled.LocalShipping
    else -> Icons.Filled.DirectionsCar
}

private fun tipoDisplay(tipo: String): String =
    tipoOpciones.find { it.first == tipo }?.second ?: tipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculosScreen(
    onBack: () -> Unit,
    viewModel: VehiculosViewModel = viewModel(),
) {
    val vehiculos by viewModel.vehiculos.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var vehiculoToDelete by remember { mutableStateOf<Vehiculo?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is VehiculosUiState.Error) {
            snackbarHostState.showSnackbar((uiState as VehiculosUiState.Error).message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehículos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = CardDark,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar vehículo", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            uiState is VehiculosUiState.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = Blue40) }

            vehiculos.isEmpty() -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No tienes vehículos registrados.\nToca + para agregar uno.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(vehiculos, key = { it.id }) { v ->
                    VehiculoCard(vehiculo = v, onDelete = { vehiculoToDelete = v })
                }
            }
        }
    }

    vehiculoToDelete?.let { v ->
        AlertDialog(
            onDismissRequest = { vehiculoToDelete = null },
            title = { Text("Eliminar vehículo") },
            text = { Text("¿Eliminar el vehículo con patente ${v.patente}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(v.id)
                    vehiculoToDelete = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { vehiculoToDelete = null }) { Text("Cancelar") }
            },
        )
    }

    if (showAddSheet) {
        AgregarVehiculoSheet(
            onDismiss = { showAddSheet = false },
            onSave = { nuevo ->
                viewModel.agregar(nuevo)
                showAddSheet = false
            },
        )
    }
}

@Composable
private fun VehiculoCard(vehiculo: Vehiculo, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Blue40),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Blue40.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = tipoIcon(vehiculo.tipoVehiculo),
                    contentDescription = null,
                    tint = Blue40,
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tipoDisplay(vehiculo.tipoVehiculo).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.5.sp,
                )
                Text(
                    text = "CATEGORÍA ${vehiculo.categoria}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .background(InputBackground, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = vehiculo.patente,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Blue40,
                            letterSpacing = 1.sp,
                        )
                    }
                    vehiculo.alias?.takeIf { it.isNotBlank() }?.let { alias ->
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = alias,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                        )
                    }
                }
            }

            Spacer(Modifier.width(4.dp))

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgregarVehiculoSheet(
    onDismiss: () -> Unit,
    onSave: (NuevoVehiculo) -> Unit,
) {
    var tipoVehiculo by remember { mutableStateOf("AUTO") }
    var categoria by remember { mutableIntStateOf(2) }
    var patente by remember { mutableStateOf("") }
    var numeroTag by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var tipoExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .imePadding(),
        ) {
            Text(
                text = "Nuevo vehículo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(20.dp))

            ExposedDropdownMenuBox(
                expanded = tipoExpanded,
                onExpandedChange = { tipoExpanded = it },
            ) {
                OutlinedTextField(
                    value = tipoOpciones.find { it.first == tipoVehiculo }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de vehículo", style = MaterialTheme.typography.bodySmall) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors(),
                    singleLine = true,
                )
                ExposedDropdownMenu(
                    expanded = tipoExpanded,
                    onDismissRequest = { tipoExpanded = false },
                ) {
                    tipoOpciones.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                            onClick = { tipoVehiculo = value; tipoExpanded = false },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = catExpanded,
                onExpandedChange = { catExpanded = it },
            ) {
                OutlinedTextField(
                    value = categoriaOpciones.find { it.first == categoria }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría de peaje", style = MaterialTheme.typography.bodySmall) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors(),
                    singleLine = true,
                )
                ExposedDropdownMenu(
                    expanded = catExpanded,
                    onDismissRequest = { catExpanded = false },
                ) {
                    categoriaOpciones.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                            onClick = { categoria = value; catExpanded = false },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = patente,
                onValueChange = { patente = it.uppercase() },
                label = { Text("Patente", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("Ej: ABCD12 o AB1234", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = fieldColors(),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = numeroTag,
                onValueChange = { numeroTag = it },
                label = { Text("Número de TAG (opcional)", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors(),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = { Text("Alias (opcional)", style = MaterialTheme.typography.bodySmall) },
                placeholder = { Text("Ej: Auto del trabajo", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = fieldColors(),
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    val userId = supabase.auth.currentUserOrNull()?.id ?: return@Button
                    onSave(
                        NuevoVehiculo(
                            userId = userId,
                            patente = patente.trim(),
                            tipoVehiculo = tipoVehiculo,
                            categoria = categoria,
                            numeroTag = numeroTag.trim().takeIf { it.isNotBlank() },
                            alias = alias.trim().takeIf { it.isNotBlank() },
                        )
                    )
                },
                enabled = patente.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardDark),
            ) {
                Text("Guardar vehículo", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = InputBackground,
    focusedContainerColor = InputBackground,
    unfocusedBorderColor = Color.Transparent,
    focusedBorderColor = Blue40,
    focusedLabelColor = Blue40,
)
