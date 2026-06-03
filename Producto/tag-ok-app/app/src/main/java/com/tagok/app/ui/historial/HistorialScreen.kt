package com.tagok.app.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary
import java.math.BigDecimal
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onBack: () -> Unit = {},
    viewModel: HistorialViewModel = viewModel(factory = HistorialViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            uiState.detalleDia != null ->
                                "${uiState.detalleDia!!.dia} de ${getMonthName(uiState.detalleDia!!.mes)} ${uiState.detalleDia!!.año}"
                            uiState.detalleMensual != null ->
                                "${getMonthName(uiState.detalleMensual!!.mes)} ${uiState.detalleMensual!!.año}"
                            uiState.selectedYear != null ->
                                "Año ${uiState.selectedYear}"
                            else -> "Historial de Viajes"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            uiState.detalleDia != null -> viewModel.clearDayDetail()
                            uiState.detalleMensual != null -> viewModel.clearMonthDetail()
                            uiState.selectedYear != null -> viewModel.clearYearDetail()
                            else -> onBack()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Blue40
                    )
                }

                uiState.error != null -> {
                    ErrorContent(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadInitialData() }
                    )
                }

                uiState.detalleDia != null -> {
                    DetalleDiaContent(
                        detalle = uiState.detalleDia!!,
                        onBack = { viewModel.clearDayDetail() }
                    )
                }

                uiState.detalleMensual != null -> {
                    CalendarioMensual(
                        detalle = uiState.detalleMensual!!,
                        onDayClick = { dia ->
                            viewModel.selectDay(uiState.detalleMensual!!.mes, dia)
                        }
                    )
                }

                uiState.selectedYear != null && uiState.detalleAnual != null -> {
                    VistaMeses(
                        detalle = uiState.detalleAnual!!,
                        onMonthClick = { viewModel.selectMonth(it) }
                    )
                }

                else -> {
                    VistaAnual(
                        resumen = uiState.resumenAnual,
                        onYearClick = { viewModel.selectYear(it) }
                    )
                }
            }
        }
    }
}

// ── Vista de Años ──────────────────────────────────────────────────────────

@Composable
private fun VistaAnual(
    resumen: List<ResumenAnual>,
    onYearClick: (Int) -> Unit
) {
    if (resumen.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay historial disponible",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(resumen) { anual ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onYearClick(anual.año) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${anual.año}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Blue40
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${anual.cantidadCruces} cruces • $${anual.totalAño}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = "Ver meses",
                        tint = TextSecondary
                    )
                }
            }
        }
    }
}

// ── Vista de Meses ─────────────────────────────────────────────────────────

@Composable
private fun VistaMeses(
    detalle: ResumenAnual,
    onMonthClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Resumen del año
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Blue40.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(label = "Total Cruces", value = "${detalle.cantidadCruces}")
                InfoItem(label = "Total Gastado", value = "$${detalle.totalAño}")
                InfoItem(label = "Meses Activos", value = "${detalle.mesesDisponibles.size}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(12) { index ->
                val mes = index + 1
                val tieneDatos = detalle.mesesDisponibles.contains(mes)
                MesCard(
                    mes = mes,
                    activo = tieneDatos,
                    onClick = { if (tieneDatos) onMonthClick(mes) }
                )
            }
        }
    }
}

@Composable
private fun MesCard(
    mes: Int,
    activo: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(enabled = activo, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activo) Blue40 else InputBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (activo) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getShortMonthName(mes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
                color = if (activo) MaterialTheme.colorScheme.onPrimary else TextSecondary
            )
        }
    }
}

// ── Calendario Mensual ─────────────────────────────────────────────────────

@Composable
private fun CalendarioMensual(
    detalle: DetalleMensual,
    onDayClick: (Int) -> Unit
) {
    val yearMonth = YearMonth.of(detalle.año, detalle.mes)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value // 1=Lunes, 7=Domingo

    // Mapa de días con datos
    val diasConDatos = detalle.dias.associate { it.dia to it }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Resumen del mes
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Blue40.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(label = "Total Mes", value = "$${detalle.totalMes}")
                InfoItem(label = "Cruces", value = "${detalle.dias.sumOf { it.cantidadCruces }}")
                InfoItem(label = "Días Activos", value = "${detalle.dias.size}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Días de la semana
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid del calendario
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Espacios vacíos antes del primer día
            items(firstDayOfWeek - 1) {
                Box(modifier = Modifier.size(40.dp))
            }

            // Días del mes
            items(daysInMonth) { index ->
                val day = index + 1
                val diaData = diasConDatos[day]

                DiaCalendario(
                    day = day,
                    tieneDatos = diaData != null,
                    cantidad = diaData?.cantidadCruces ?: 0,
                    total = diaData?.totalDia,
                    onClick = { if (diaData != null) onDayClick(day) }
                )
            }
        }
    }
}

@Composable
private fun DiaCalendario(
    day: Int,
    tieneDatos: Boolean,
    cantidad: Int,
    total: Double?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                when {
                    tieneDatos -> Blue40.copy(alpha = 0.2f)
                    else -> InputBackground
                }
            )
            .border(
                width = if (tieneDatos) 2.dp else 0.dp,
                color = if (tieneDatos) Blue40 else InputBackground,
                shape = CircleShape
            )
            .clickable(enabled = tieneDatos, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$day",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (tieneDatos) FontWeight.Bold else FontWeight.Normal,
                color = if (tieneDatos) Blue40 else TextSecondary,
                fontSize = 14.sp
            )
            if (tieneDatos && cantidad > 0) {
                Text(
                    text = "$cantidad",
                    style = MaterialTheme.typography.labelSmall,
                    color = Blue40,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// ── Detalle del Día ────────────────────────────────────────────────────────

@Composable
private fun DetalleDiaContent(
    detalle: DetalleDia,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Resumen del día
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blue40.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${detalle.dia} de ${getMonthName(detalle.mes)} ${detalle.año}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Blue40
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoItem(label = "Cruces", value = "${detalle.cantidadCruces}")
                        InfoItem(label = "Total", value = "$${detalle.totalDia}")
                    }
                }
            }
        }

        // Título de la lista de cruces
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Detalle de cruces",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }

        // Lista de cruces
        items(detalle.cruces) { cruce ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cruce.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$${cruce.valor}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Blue40,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${cruce.autopista} • ${cruce.codigo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tarifa: ${cruce.tipoTarifa}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = cruce.tipoVehiculo,
                            style = MaterialTheme.typography.labelSmall,
                            color = Blue40
                        )
                    }

                    if (cruce.horaFechaCruce.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Hora: ${cruce.horaFechaCruce}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ── Componentes compartidos ────────────────────────────────────────────────

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error al cargar el historial",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Blue40
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

// ── Utilidades ─────────────────────────────────────────────────────────────

private fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Enero"
        2 -> "Febrero"
        3 -> "Marzo"
        4 -> "Abril"
        5 -> "Mayo"
        6 -> "Junio"
        7 -> "Julio"
        8 -> "Agosto"
        9 -> "Septiembre"
        10 -> "Octubre"
        11 -> "Noviembre"
        12 -> "Diciembre"
        else -> "Mes $month"
    }
}

private fun getShortMonthName(month: Int): String {
    return when (month) {
        1 -> "Ene"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Abr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Ago"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dic"
        else -> "M$month"
    }
}