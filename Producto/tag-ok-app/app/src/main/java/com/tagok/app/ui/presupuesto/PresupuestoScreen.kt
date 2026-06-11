package com.tagok.app.ui.presupuesto

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale

private val NavyBlue    = Color(0xFF172955)
private val AccentBlue  = Color(0xFF1C42B1)
private val PageBg      = Color(0xFFF4F6FB)
private val DividerGray = Color(0xFFE5E7EB)
private val TextDark    = Color(0xFF111827)
private val OrangeWarn  = Color(0xFFF59E0B)
private val RedAlert    = Color(0xFFEF4444)
private val GreenSafe   = Color(0xFF22C55E)
private val LightBlueBg = Color(0xFFEEF2FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestoScreen(viewModel: PresupuestoViewModel = viewModel(factory = PresupuestoViewModel.Factory)) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize().background(PageBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Header ──────────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text       = "Presupuesto",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 22.sp,
                    color      = Color(0xFF1A1A2E),
                )
                Text(
                    text     = "Control de gastos en peajes",
                    fontSize = 13.sp,
                    color    = Color.Gray,
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Filtro por vehículo (card estilo "VEHÍCULO ACTIVO") ──────────────
            Card(
                modifier  = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text(
                        text          = "VEHÍCULO",
                        fontSize      = 12.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = AccentBlue,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    VehiculoFiltroRow(state = state, onSeleccionar = viewModel::seleccionarVehiculo)
                }
            }

            Spacer(Modifier.height(12.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NavyBlue, strokeWidth = 2.dp)
                }
            } else if (state.presupuestoActual == null) {
                EmptyState(onConfigurar = viewModel::abrirEditSheet)
            } else {
                val presupuesto = state.presupuestoActual!!
                // TODO: reemplazar con datos reales del historial (MongoDB) cuando estén disponibles
                val gastoActual = 0
                val peajesCount = 0
                val porcentaje  = if (presupuesto.montoMensual > 0)
                    (gastoActual.toFloat() / presupuesto.montoMensual).coerceIn(0f, 1f)
                else 0f

                BudgetCard(
                    montoMaximo = presupuesto.montoMensual,
                    gastoActual = gastoActual,
                    peajesCount = peajesCount,
                    porcentaje  = porcentaje,
                )

                Spacer(Modifier.height(12.dp))

                AlertasCard(
                    umbral1          = presupuesto.umbralAlerta1,
                    umbral2          = presupuesto.umbralAlerta2,
                    porcentajeActual = (porcentaje * 100).toInt(),
                )

                Spacer(Modifier.height(20.dp))

                // Botón editar — mismo estilo que ActionButton en Home
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyBlue),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(
                        onClick   = viewModel::abrirEditSheet,
                        modifier  = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            "Editar presupuesto",
                            fontSize   = 15.sp,
                            color      = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (state.errorMsg != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title            = { Text("Error de validación", fontWeight = FontWeight.SemiBold) },
            text             = { Text(state.errorMsg!!) },
            confirmButton    = {
                TextButton(onClick = viewModel::clearError) {
                    Text("Entendido", color = AccentBlue, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    if (state.showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = viewModel::cerrarEditSheet,
            sheetState       = sheetState,
            containerColor   = Color.White,
        ) {
            EditPresupuestoSheet(state = state, viewModel = viewModel)
        }
    }
}

// ─── Filtro de vehículos ──────────────────────────────────────────────────────
@Composable
private fun VehiculoFiltroRow(
    state: PresupuestoUiState,
    onSeleccionar: (String?) -> Unit,
) {
    LazyRow(
        contentPadding        = PaddingValues(end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FiltroChip(
                label    = "Global",
                selected = state.vehiculoIdFiltro == null,
                onClick  = { onSeleccionar(null) },
            )
        }
        items(state.vehiculos) { v ->
            FiltroChip(
                label    = v.alias?.takeIf { it.isNotBlank() } ?: v.patente,
                selected = state.vehiculoIdFiltro == v.id,
                onClick  = { onSeleccionar(v.id) },
            )
        }
    }
}

@Composable
private fun FiltroChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, fontSize = 13.sp) },
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor = NavyBlue,
            selectedLabelColor     = Color.White,
            containerColor         = Color.White,
            labelColor             = Color.Gray,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled             = true,
            selected            = selected,
            borderColor         = DividerGray,
            selectedBorderColor = NavyBlue,
        ),
    )
}

// ─── Tarjeta principal con donut chart ───────────────────────────────────────
@Composable
private fun BudgetCard(
    montoMaximo: Int,
    gastoActual: Int,
    peajesCount: Int,
    porcentaje: Float,
) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier            = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Label sección
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text          = "PRESUPUESTO MENSUAL",
                    fontSize      = 12.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = AccentBlue,
                    letterSpacing = 1.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(montoMaximo.toCLP(), fontWeight = FontWeight.Bold, fontSize = 32.sp, color = TextDark)
            Text("Monto máximo por mes", fontSize = 13.sp, color = Color.Gray)

            Spacer(Modifier.height(24.dp))
            Text("Llevas gastado", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                DonutChart(
                    progress   = porcentaje,
                    modifier   = Modifier.size(140.dp),
                    porcentaje = (porcentaje * 100).toInt(),
                )
                Spacer(Modifier.width(24.dp))
                Column {
                    Text(
                        text       = gastoActual.toCLP(),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 22.sp,
                        color      = progressColor(porcentaje),
                    )
                    Text(
                        text     = "$peajesCount peajes",
                        fontSize = 13.sp,
                        color    = Color.Gray,
                    )
                }
            }
        }
    }
}

// ─── Donut chart ─────────────────────────────────────────────────────────────
@Composable
private fun DonutChart(progress: Float, porcentaje: Int, modifier: Modifier = Modifier) {
    val arcColor   = progressColor(progress)
    val trackColor = DividerGray

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val stroke   = 22.dp.toPx()
                    val diameter = size.minDimension - stroke
                    val topLeft  = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                    val arcSize  = Size(diameter, diameter)

                    drawArc(
                        color      = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter  = false,
                        topLeft    = topLeft,
                        size       = arcSize,
                        style      = Stroke(stroke, cap = StrokeCap.Round),
                    )
                    if (progress > 0f) {
                        drawArc(
                            color      = arcColor,
                            startAngle = -90f,
                            sweepAngle = 360f * progress.coerceIn(0f, 1f),
                            useCenter  = false,
                            topLeft    = topLeft,
                            size       = arcSize,
                            style      = Stroke(stroke, cap = StrokeCap.Round),
                        )
                    }
                },
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "$porcentaje%",
                fontWeight = FontWeight.Bold,
                fontSize   = 26.sp,
                color      = TextDark,
            )
            Text(text = "gastado", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

private fun progressColor(progress: Float): Color {
    val p = progress.coerceIn(0f, 1f)
    return when {
        p < 0.60f -> GreenSafe // 100% Verde hasta el 60%
        p < 0.85f -> {
            // Transición suave de Verde a Naranja entre 60% y 85%
            val factor = (p - 0.60f) / 0.25f
            lerp(GreenSafe, OrangeWarn, factor)
        }
        else -> {
            // Transición suave de Naranja a Rojo entre 85% y 100%
            val factor = (p - 0.85f) / 0.15f
            lerp(OrangeWarn, RedAlert, factor)
        }
    }
}

// ─── Tarjeta de alertas ───────────────────────────────────────────────────────
@Composable
private fun AlertasCard(umbral1: Int, umbral2: Int, porcentajeActual: Int) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier              = Modifier.padding(18.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightBlueBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.NotificationsActive,
                    null,
                    tint     = AccentBlue,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "ALERTAS CONFIGURADAS",
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = AccentBlue,
                    letterSpacing = 0.8.sp,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text     = "Notificación al $umbral1% · al $umbral2%",
                    fontSize = 12.sp,
                    color    = Color.Gray,
                )
            }
            val proximoUmbral = listOf(umbral1, umbral2).firstOrNull { it > porcentajeActual }
            if (proximoUmbral != null) {
                Box(
                    modifier = Modifier
                        .background(LightBlueBg, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text       = "Próx. $proximoUmbral%",
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = AccentBlue,
                    )
                }
            }
        }
    }
}

// ─── Empty state ─────────────────────────────────────────────────────────────
@Composable
private fun EmptyState(onConfigurar: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier            = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier         = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightBlueBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Wallet,
                    null,
                    tint     = AccentBlue,
                    modifier = Modifier.size(34.dp),
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Sin presupuesto configurado",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = TextDark,
                textAlign  = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Define tu límite mensual para llevar\nel control de tus gastos en peajes.",
                fontSize  = 13.sp,
                color     = Color.Gray,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyBlue),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(
                    onClick  = onConfigurar,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Configurar presupuesto",
                        fontSize   = 15.sp,
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

// ─── Bottom sheet de edición ──────────────────────────────────────────────────
@Composable
private fun EditPresupuestoSheet(state: PresupuestoUiState, viewModel: PresupuestoViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
    ) {
        Text(
            text       = if (state.presupuestoActual == null) "Configurar presupuesto" else "Editar presupuesto",
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = TextDark,
        )
        val vehiculoLabel = state.vehiculos
            .find { it.id == state.vehiculoIdFiltro }
            ?.let { it.alias?.takeIf { a -> a.isNotBlank() } ?: it.patente }
            ?: "Global"
        Text("Para: $vehiculoLabel", fontSize = 12.sp, color = Color.Gray)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value           = state.formMonto,
            onValueChange   = viewModel::updateFormMonto,
            label           = { Text("Monto máximo mensual (CLP)", fontSize = 12.sp) },
            modifier        = Modifier.fillMaxWidth(),
            singleLine      = true,
            prefix          = { Text("$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape           = RoundedCornerShape(12.dp),
            colors          = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = NavyBlue,
                unfocusedBorderColor = DividerGray,
                focusedLabelColor    = NavyBlue,
            ),
        )

        Spacer(Modifier.height(24.dp))

        UmbralSlider(
            label   = "Primera alerta",
            value   = state.formUmbral1,
            onValue = viewModel::updateUmbral1,
            range   = 10f..99f,
            color   = getThresholdColor(state.formUmbral1),
        )

        Spacer(Modifier.height(16.dp))

        UmbralSlider(
            label   = "Segunda alerta",
            value   = state.formUmbral2,
            onValue = viewModel::updateUmbral2,
            range   = 10f..100f,
            color   = getThresholdColor(state.formUmbral2),
        )

        Spacer(Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextButton(
                onClick  = viewModel::cerrarEditSheet,
                modifier = Modifier.weight(1f),
                colors   = ButtonDefaults.textButtonColors(contentColor = Color.Gray),
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick  = viewModel::guardar,
                enabled  = !state.isSaving,
                modifier = Modifier.weight(2f).height(50.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NavyBlue),
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Guardar", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun UmbralSlider(
    label: String,
    value: Float,
    onValue: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    color: Color,
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Medium)
            Text(
                "${value.toInt()}%",
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold,
                color      = color,
            )
        }
        Slider(
            value         = value,
            onValueChange = onValue,
            valueRange    = range,
            steps         = 0,
            colors        = SliderDefaults.colors(
                thumbColor         = color,
                activeTrackColor   = color,
                inactiveTrackColor = DividerGray,
            ),
        )
    }
}

private fun getThresholdColor(value: Float): Color {
    val t = (value / 100f).coerceIn(0f, 1f)
    return when {
        t < 0.60f -> GreenSafe // 100% Verde hasta el 60%
        t < 0.85f -> {
            // Transición suave de Verde a Naranja entre 60% y 85%
            val factor = (t - 0.60f) / 0.25f
            lerp(GreenSafe, OrangeWarn, factor)
        }
        else -> {
            // Transición suave de Naranja a Rojo entre 85% y 100%
            val factor = (t - 0.85f) / 0.15f
            lerp(OrangeWarn, RedAlert, factor)
        }
    }
}

private fun Int.toCLP(): String {
    val fmt = NumberFormat.getNumberInstance(Locale("es", "CL"))
    fmt.minimumFractionDigits = 0
    fmt.maximumFractionDigits = 0
    return "$${fmt.format(this)}"
}
