package com.tagok.app.ui.boleta

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.domain.model.boleta.Boleta
import com.tagok.app.domain.model.boleta.BoletaItem
import com.tagok.app.ui.boleta.components.AutopistaMultiSelect
import com.tagok.app.ui.boleta.components.DateRangeSelector
import com.tagok.app.ui.boleta.components.PatenteSelector
import com.tagok.app.ui.common.ErrorContent
import com.tagok.app.ui.common.LoadingState
import com.tagok.app.ui.common.ScreenLifecycle
import com.tagok.app.ui.common.display
import com.tagok.app.ui.theme.AccentBlue
import com.tagok.app.ui.theme.LightBlueBg
import com.tagok.app.ui.theme.NavyBlue
import com.tagok.app.ui.theme.PageBg
import com.tagok.app.ui.theme.TextDark

@Composable
fun BoletaScreen(
    onVerificarFactura: (
        patente: String,
        fechaDesde: kotlinx.datetime.LocalDate,
        fechaHasta: kotlinx.datetime.LocalDate,
        autopistas: List<String>) -> Unit = { _, _, _, _ -> },
    viewModel: BoletaViewModel = viewModel(factory = BoletaViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenLifecycle(viewModel = viewModel)

    Box(modifier = Modifier.fillMaxSize().background(PageBg))
    {
        when
        {
            uiState.isLoading && uiState.vehiculos.isEmpty() -> {
                LoadingState(message = "Cargando datos...")
            }

            uiState.error != null && uiState.vehiculos.isEmpty() -> {
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

        if (uiState.isLoading && uiState.vehiculos.isNotEmpty())
        {
            LoadingOverlay()
        }
    }

    if (uiState.error != null && uiState.vehiculos.isNotEmpty())
    {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error", fontWeight = FontWeight.SemiBold) },
            text = { Text(uiState.error ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("Entendido", color = AccentBlue, fontWeight = FontWeight.SemiBold)
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
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp))
        {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp))
            {
                CircularProgressIndicator(color = NavyBlue, strokeWidth = 2.dp)
                Text(
                    text = "Generando boleta...",
                    fontSize = 13.sp,
                    color = TextDark)
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
        autopistas: List<String>) -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()))
    {
        Spacer(Modifier.height(16.dp))

        // ── Header ──────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Generar Boleta",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF1A1A2E))
            Text(
                text = "Tu boleta de cruces y peajes",
                fontSize = 13.sp,
                color = Color.Gray)
        }

        Spacer(Modifier.height(20.dp))

        SectionCard(title = "VEHÍCULO") {
            PatenteSelector(
                vehiculos = uiState.vehiculos,
                selected = uiState.patenteSeleccionada,
                onPatenteSelected = onPatenteSelected)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard(title = "PERÍODO") {
            DateRangeSelector(
                fechaDesde = uiState.fechaDesde,
                fechaHasta = uiState.fechaHasta,
                onDesdeChanged = onFechaDesdeChanged,
                onHastaChanged = onFechaHastaChanged)
        }

        Spacer(Modifier.height(12.dp))

        SectionCard(title = "AUTOPISTAS (OPCIONAL)") {
            AutopistaMultiSelect(
                autopistas = uiState.autopistas,
                selected = uiState.autopistasSeleccionadas,
                onToggle = onToggleAutopista)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onGenerarBoleta,
            enabled = !uiState.isLoading && uiState.patenteSeleccionada.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue))
        {
            Text(
                text = if (uiState.boleta != null) "Regenerar boleta" else "Generar boleta",
                fontWeight = FontWeight.SemiBold,
                color = Color.White)
        }

        uiState.boleta?.let { boleta ->
            Spacer(Modifier.height(16.dp))
            BoletaResultCard(boleta = boleta)

            Spacer(Modifier.height(12.dp))

            // Atajo a la verificación con IA: compara esta boleta contra la
            // factura (PDF o foto) que entrega la concesionaria
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LightBlueBg),
                contentAlignment = Alignment.Center)
            {
                TextButton(
                    onClick = {
                        onVerificarFactura(
                            uiState.patenteSeleccionada,
                            uiState.fechaDesde,
                            uiState.fechaHasta,
                            uiState.autopistasSeleccionadas)
                    },
                    modifier = Modifier.fillMaxSize())
                {
                    Icon(
                        Icons.Default.DocumentScanner,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Verificar factura con IA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentBlue)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─── Tarjeta de sección (mismo estilo que la tarjeta "VEHÍCULO" de Presupuesto) ──
@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit)
{
    Card(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp))
    {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp))
        {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AccentBlue,
                letterSpacing = 1.sp)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

// ─── Resultado de la boleta generada ─────────────────────────────────────────
@Composable
private fun BoletaResultCard(boleta: Boleta)
{
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(boleta) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }))
    {
        Card(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp))
        {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp))
            {
                Text(
                    text = "BOLETA GENERADA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue,
                    letterSpacing = 1.sp)

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "$${String.format("%.2f", boleta.total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = TextDark)
                Text(
                    text = "Total a pagar · ${boleta.items.size} transacciones",
                    fontSize = 13.sp,
                    color = Color.Gray)

                if (boleta.items.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp))
                    {
                        items(boleta.items) { item ->
                            BoletaItemRow(item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoletaItemRow(item: BoletaItem)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PageBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            Column(modifier = Modifier.weight(1f))
            {
                Text(
                    text = item.nombre,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)

                Spacer(Modifier.height(4.dp))

                Text(
                    text = item.autopista,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)

                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Text(
                        text = item.horaCruce.display(),
                        fontSize = 11.sp,
                        color = Color.Gray)

                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Gray))

                    Text(
                        text = item.tipoTarifa,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentBlue)
                }
            }

            Spacer(Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightBlueBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp))
            {
                Text(
                    text = "$${String.format("%.2f", item.valor)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue)
            }
        }
    }
}
