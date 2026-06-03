package com.tagok.app.ui.historial

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.domain.model.history.DetalleDia
import com.tagok.app.domain.model.history.DetalleMensual
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary
import java.time.LocalDate
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

    // Extraer valores de forma segura
    val detalleDia = uiState.detalleDia
    val detalleMensual = uiState.detalleMensual
    val detalleAnual = uiState.detalleAnual

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            detalleDia != null ->
                                "${detalleDia.dia} de ${getMonthName(detalleDia.mes)} ${detalleDia.año}"
                            detalleMensual != null ->
                                "${getMonthName(detalleMensual.mes)} ${detalleMensual.año}"
                            uiState.selectedYear != null ->
                                "Año ${uiState.selectedYear}"
                            else -> "Historial de Viajes"
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when {
                            detalleDia != null -> viewModel.clearDayDetail()
                            detalleMensual != null -> viewModel.clearMonthDetail()
                            uiState.selectedYear != null -> viewModel.clearYearDetail()
                            else -> onBack()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.error != null -> {
                    val errorMsg = uiState.error ?: "Error desconocido"
                    ErrorContent(
                        message = errorMsg,
                        onRetry = { viewModel.loadInitialData() }
                    )
                }

                detalleDia != null -> {
                    DetalleDiaContent(
                        detalle = detalleDia,
                        onBack = { viewModel.clearDayDetail() }
                    )
                }

                detalleMensual != null -> {
                    CalendarioMensual(
                        detalle = detalleMensual,
                        onDayClick = { dia ->
                            viewModel.selectDay(detalleMensual.mes, dia)
                        }
                    )
                }

                uiState.selectedYear != null && detalleAnual != null -> {
                    VistaMeses(
                        detalle = detalleAnual,
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

// ── Loading ────────────────────────────────────────────────────────────────

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Blue40)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando historial...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
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
        EmptyState()
        return
    }

    val maxCruces = resumen.maxOfOrNull { it.cantidadCruces } ?: 1

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ResumenTotalCard(resumen = resumen)
        }

        items(resumen, key = { it.año }) { anual ->
            val animacionVisible = remember { Animatable(0f) }

            LaunchedEffect(anual.año) {
                animacionVisible.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = resumen.indexOf(anual) * 100
                    )
                )
            }

            AnimatedYearCard(
                anual = anual,
                maxCruces = maxCruces,
                onClick = { onYearClick(anual.año) },
                modifier = Modifier
                    .alpha(animacionVisible.value)
                    .scale(0.8f + (0.2f * animacionVisible.value))
            )
        }
    }
}

@Composable
private fun ResumenTotalCard(resumen: List<ResumenAnual>) {
    val totalCruces = resumen.sumOf { it.cantidadCruces }
    val totalGastado = resumen.sumOf { it.totalAño }
    val añosActivos = resumen.size

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blue40),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Resumen Total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TotalItem(
                    icon = Icons.Filled.Toll,
                    value = "$totalCruces",
                    label = "Cruces Totales"
                )
                TotalItem(
                    icon = Icons.Filled.MonetizationOn,
                    value = totalGastado.formatCurrency(),
                    label = "Total Gastado"
                )
                TotalItem(
                    icon = Icons.Filled.DateRange,
                    value = "$añosActivos",
                    label = "Años"
                )
            }
        }
    }
}

@Composable
private fun TotalItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun AnimatedYearCard(
    anual: ResumenAnual,
    maxCruces: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val intensidad = if (maxCruces > 0) (anual.cantidadCruces.toFloat() / maxCruces) else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${anual.año}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Blue40
                    )
                    if (intensidad > 0.7f) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Blue40.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Más activo",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Blue40,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Toll,
                        contentDescription = "Cruces",
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${anual.cantidadCruces} cruces",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Filled.MonetizationOn,
                        contentDescription = "Total",
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = anual.totalAño.formatCurrency(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Ver meses",
                tint = Blue40.copy(alpha = 0.5f)
            )
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
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Blue40, Blue40.copy(alpha = 0.8f))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${detalle.año}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TotalItem(
                            icon = Icons.Filled.Toll,
                            value = "${detalle.cantidadCruces}",
                            label = "Cruces"
                        )
                        TotalItem(
                            icon = Icons.Filled.MonetizationOn,
                            value = detalle.totalAño.formatCurrency(),
                            label = "Total"
                        )
                        TotalItem(
                            icon = Icons.Filled.CalendarMonth,
                            value = "${detalle.mesesDisponibles.size}",
                            label = "Meses"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(12) { index ->
                val mes = index + 1
                val tieneDatos = detalle.mesesDisponibles.contains(mes)

                AnimatedMonthCard(
                    mes = mes,
                    activo = tieneDatos,
                    onClick = { if (tieneDatos) onMonthClick(mes) }
                )
            }
        }
    }
}

@Composable
private fun AnimatedMonthCard(
    mes: Int,
    activo: Boolean,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = mes * 30,
                easing = FastOutSlowInEasing
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .scale(scale.value)
            .clickable(enabled = activo, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activo) Blue40 else InputBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (activo) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = getShortMonthName(mes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium,
                    color = if (activo) Color.White else TextSecondary
                )
                if (activo) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        Icons.Filled.TrendingUp,
                        contentDescription = "Tiene datos",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
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
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value
    val diasConDatos = detalle.dias.associate { it.dia to it }
    val maxCruces = detalle.dias.maxOfOrNull { it.cantidadCruces } ?: 1
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Blue40.copy(alpha = 0.1f),
                                Blue40.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoItem(
                        icon = Icons.Filled.MonetizationOn,
                        label = "Total Mes",
                        value = detalle.totalMes.formatCurrency()
                    )
                    InfoItem(
                        icon = Icons.Filled.Toll,
                        label = "Cruces",
                        value = "${detalle.dias.sumOf { it.cantidadCruces }}"
                    )
                    InfoItem(
                        icon = Icons.Filled.Today,
                        label = "Días Activos",
                        value = "${detalle.dias.size}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = InputBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Menos",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                Blue40.copy(alpha = 0.1f + (index * 0.22f))
                            )
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Más",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (day == "Dom") MaterialTheme.colorScheme.error else TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(firstDayOfWeek - 1) {
                Box(modifier = Modifier.size(44.dp))
            }

            items(daysInMonth) { index ->
                val day = index + 1
                val diaData = diasConDatos[day]
                val intensidad = if (diaData != null && maxCruces > 0) {
                    diaData.cantidadCruces.toFloat() / maxCruces
                } else 0f
                val isToday = day == today.dayOfMonth &&
                        yearMonth.year == today.year &&
                        yearMonth.monthValue == today.monthValue

                EnhancedDiaCalendario(
                    day = day,
                    tieneDatos = diaData != null,
                    cantidad = diaData?.cantidadCruces ?: 0,
                    intensidad = intensidad,
                    isToday = isToday,
                    onClick = { if (diaData != null) onDayClick(day) }
                )
            }
        }
    }
}

@Composable
private fun EnhancedDiaCalendario(
    day: Int,
    tieneDatos: Boolean,
    cantidad: Int,
    intensidad: Float,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !tieneDatos && isToday -> Blue40.copy(alpha = 0.05f)
        !tieneDatos -> Color.White
        intensidad < 0.2f -> Blue40.copy(alpha = 0.1f)
        intensidad < 0.4f -> Blue40.copy(alpha = 0.25f)
        intensidad < 0.6f -> Blue40.copy(alpha = 0.45f)
        intensidad < 0.8f -> Blue40.copy(alpha = 0.65f)
        else -> Blue40
    }

    val borderColor = when {
        isToday -> Blue40
        !tieneDatos -> InputBackground
        else -> Blue40.copy(alpha = 0.3f + (intensidad * 0.7f))
    }

    val textColor = when {
        !tieneDatos && !isToday -> TextSecondary
        intensidad > 0.6f -> Color.White
        else -> Blue40
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isToday) 2.dp else if (tieneDatos) 1.5.dp else 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(enabled = tieneDatos, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$day",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (tieneDatos || isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                fontSize = 14.sp
            )
            if (tieneDatos && cantidad > 0) {
                Text(
                    text = "$cantidad",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (intensidad > 0.6f) Color.White.copy(alpha = 0.8f) else Blue40.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
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
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Blue40, Blue40.copy(alpha = 0.9f))
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${detalle.dia}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${getMonthName(detalle.mes)} ${detalle.año}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TotalItem(
                                icon = Icons.Filled.Toll,
                                value = "${detalle.cantidadCruces}",
                                label = "Cruces"
                            )
                            TotalItem(
                                icon = Icons.Filled.MonetizationOn,
                                value = detalle.totalDia.formatCurrency(),
                                label = "Total"
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detalle de cruces",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Blue40.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${detalle.cruces.size} cruces",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Blue40,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(detalle.cruces) { cruce ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Blue40.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                cruce.tipoVehiculo.contains("MOTO", ignoreCase = true) -> Icons.Filled.TwoWheeler
                                cruce.tipoVehiculo.contains("BUS", ignoreCase = true) -> Icons.Filled.DirectionsBus
                                cruce.tipoVehiculo.contains("CAMION", ignoreCase = true) -> Icons.Filled.LocalShipping
                                else -> Icons.Filled.DirectionsCar
                            },
                            contentDescription = cruce.tipoVehiculo,
                            tint = Blue40,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cruce.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = cruce.horaFechaCruce.takeLast(5),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "Tarifa: ${cruce.tipoTarifa}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = cruce.valor.formatCurrency(),
                            style = MaterialTheme.typography.titleMedium,
                            color = Blue40,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = cruce.tipoVehiculo,
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
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.History,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextSecondary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay historial disponible",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tus viajes aparecerán aquí",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error al cargar el historial",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = Blue40.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
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

private fun Double.formatCurrency(): String {
    return when {
        this >= 1_000_000 -> "$${"%.1f".format(this / 1_000_000)}M"
        this >= 1_000 -> "$${"%.0f".format(this / 1_000)}K"
        else -> "$${"%.0f".format(this)}"
    }
}

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