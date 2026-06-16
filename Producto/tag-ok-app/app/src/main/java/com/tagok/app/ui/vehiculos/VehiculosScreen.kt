package com.tagok.app.ui.vehiculos

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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.tagok.app.domain.model.vehiculo.NuevoVehiculo
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.supabase
import com.tagok.app.ui.theme.AccentBlue
import com.tagok.app.ui.theme.LightBlueBg
import com.tagok.app.ui.theme.NavyBlue
import com.tagok.app.ui.theme.PageBg
import com.tagok.app.ui.theme.TextDark
import io.github.jan.supabase.auth.auth

private val tipoOpciones = listOf(
    "AUTO" to "Automóvil",
    "MOTO" to "Motocicleta",
    "CAMIONETA" to "Camioneta",
    "BUS" to "Bus",
    "CAMION" to "Camión",
    "CAMION_REMOLQUE" to "Camión con remolque")

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
    viewModel: VehiculosViewModel = viewModel(factory = VehiculosViewModel.Factory))
{
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
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                title = { Text("Vehículos", fontWeight = FontWeight.Bold, color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = NavyBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PageBg),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = NavyBlue,
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
            ) { CircularProgressIndicator(color = NavyBlue) }

            vehiculos.isEmpty() -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No tienes vehículos registrados.\nToca + para agregar uno.",
                    color = Color.Gray,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                    .background(LightBlueBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = tipoIcon(vehiculo.tipoVehiculo),
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tipoDisplay(vehiculo.tipoVehiculo).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    letterSpacing = 0.5.sp,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .background(LightBlueBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = vehiculo.patente,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentBlue,
                            letterSpacing = 1.sp,
                        )
                    }
                    vehiculo.alias?.takeIf { it.isNotBlank() }?.let { alias ->
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = alias,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                    }
                }
            }

            Spacer(Modifier.width(4.dp))

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Gray,
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
    var patente by remember { mutableStateOf("") }
    var numeroTag by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var tipoExpanded by remember { mutableStateOf(false) }

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
                            numeroTag = numeroTag.trim().takeIf { it.isNotBlank() },
                            alias = alias.trim().takeIf { it.isNotBlank() }
                        )
                    )
                },
                enabled = patente.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
            ) {
                Text("Guardar vehículo", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = LightBlueBg,
    focusedContainerColor = LightBlueBg,
    unfocusedBorderColor = Color.Transparent,
    focusedBorderColor = NavyBlue,
    focusedLabelColor = NavyBlue,
)
