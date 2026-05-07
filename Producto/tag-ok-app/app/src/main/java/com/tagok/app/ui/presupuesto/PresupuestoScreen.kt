package com.tagok.app.ui.presupuesto

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary
import kotlinx.coroutines.delay

// ─── Colores semánticos ───────────────────────────────────────────────────────
private val GreenOk    = Color(0xFF10B981)
private val RedAlert   = Color(0xFFEF4444)
private val OrangeWarn = Color(0xFFF59E0B)
private val GreenBg    = Color(0xFFECFDF5)
private val RedBg      = Color(0xFFFEF2F2)
private val OrangeBg   = Color(0xFFFFFBEB)
private val SurfaceGray = Color(0xFFF3F4F6)
private val DividerGray = Color(0xFFE5E7EB)
private val TextDark   = Color(0xFF111827)

// ─── Modelos mock ─────────────────────────────────────────────────────────────
private enum class EstadoCruce { OK, DISCREPANCIA, SIN_REGISTRO }

private data class CruceComparado(
    val fecha: String,
    val portico: String,
    val concesionaria: String,
    val montoTagOk: Int?,
    val montoBoleta: Int?,
    val estado: EstadoCruce,
)

private data class ResumenConcesionaria(
    val nombre: String,
    val tagOk: Int,
    val boleta: Int,
)

private data class MesTendencia(val mes: String, val tagOk: Int, val boleta: Int)

private val crucesMock = listOf(
    CruceComparado("02 may", "Km 12 · Autopista Central",       "Autopista Central", 1290, 1290, EstadoCruce.OK),
    CruceComparado("03 may", "Vespucio · Costanera Norte",      "Costanera Norte",   2150, 2350, EstadoCruce.DISCREPANCIA),
    CruceComparado("05 may", "Km 8 · Autopista Central",        "Autopista Central", 1290, 1290, EstadoCruce.OK),
    CruceComparado("07 may", "Kennedy · Vespucio Sur",          "Vespucio Sur",      1870, 1870, EstadoCruce.OK),
    CruceComparado("09 may", "Las Rejas · Costanera Norte",     "Costanera Norte",   2150, 2150, EstadoCruce.OK),
    CruceComparado("12 may", "Km 12 · Autopista Central",       "Autopista Central", 1290, 1290, EstadoCruce.OK),
    CruceComparado("14 may", "Km 12 · Autopista Central",       "Autopista Central", null, 1290, EstadoCruce.SIN_REGISTRO),
)

private val concesionariasMock = listOf(
    ResumenConcesionaria("Autopista Central", 3870, 5160),
    ResumenConcesionaria("Costanera Norte",   4300, 4500),
    ResumenConcesionaria("Vespucio Sur",      1870, 1870),
)

private val tendenciaMock = listOf(
    MesTendencia("dic", 8200,  8200),
    MesTendencia("ene", 9100,  9300),
    MesTendencia("feb", 7800,  7800),
    MesTendencia("mar", 10500, 10900),
    MesTendencia("abr", 8900,  9100),
    MesTendencia("may", 10040, 11530),
)

private enum class EstadoBoleta { NINGUNA, ANALIZANDO, ANALIZADA }

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun PresupuestoScreen() {
    var estadoBoleta by remember { mutableStateOf(EstadoBoleta.NINGUNA) }

    LaunchedEffect(estadoBoleta) {
        if (estadoBoleta == EstadoBoleta.ANALIZANDO) {
            delay(3000)
            estadoBoleta = EstadoBoleta.ANALIZADA
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(SurfaceGray),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        // 1 · Header
        item { HeaderSection() }

        // 2 · Resumen general (solo post-análisis)
        item {
            AnimatedVisibility(
                visible = estadoBoleta == EstadoBoleta.ANALIZADA,
                enter = fadeIn() + slideInVertically { -it / 2 },
            ) { ResumenGeneralSection() }
        }

        // 3 · Por concesionaria (solo post-análisis)
        item {
            AnimatedVisibility(
                visible = estadoBoleta == EstadoBoleta.ANALIZADA,
                enter = fadeIn(tween(400)) + slideInVertically { -it / 2 },
            ) { ConcesionariasSection() }
        }

        // 4 · Tendencia mensual (solo post-análisis)
        item {
            AnimatedVisibility(
                visible = estadoBoleta == EstadoBoleta.ANALIZADA,
                enter = fadeIn(tween(600)) + slideInVertically { -it / 2 },
            ) { TendenciaSection() }
        }

        // 5 · Botón de importar / loading / badge de éxito
        item {
            ImportarBoleta(
                estado = estadoBoleta,
                onImportar = { estadoBoleta = EstadoBoleta.ANALIZANDO },
            )
        }

        // 6 · Insight IA
        item {
            AnimatedVisibility(
                visible = estadoBoleta == EstadoBoleta.ANALIZADA,
                enter = fadeIn(tween(700)),
            ) { InsightIACard() }
        }

        // 7 · Título detalle
        if (estadoBoleta == EstadoBoleta.ANALIZADA) {
            item {
                SectionTitle(
                    text = "Detalle de cruces",
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 2.dp),
                )
            }
            items(crucesMock) { CruceRow(it) }
        }
    }
}

// ─── 1 · Header ───────────────────────────────────────────────────────────────
@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text("Gastos del mes", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextDark)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Mayo 2026", fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Blue40)
            Icon(Icons.Filled.ArrowDropDown, null, tint = Blue40, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Contrasta tu historial de pórticos con tu boleta mensual",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
}

// ─── 2 · Resumen general ──────────────────────────────────────────────────────
@Composable
private fun ResumenGeneralSection() {
    val totalTagOk  = crucesMock.mapNotNull { it.montoTagOk }.sum()
    val totalBoleta = crucesMock.mapNotNull { it.montoBoleta }.sum()
    val diferencia  = totalBoleta - totalTagOk

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
        SectionTitle("Resumen del mes")
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ResumenCard(Modifier.weight(1f), "TAG OK registró",   "$${fmt(totalTagOk)}",  Blue40,     InputBackground)
            ResumenCard(Modifier.weight(1f), "Concesionaria cobró", "$${fmt(totalBoleta)}", RedAlert, RedBg)
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ResumenCard(Modifier.weight(1f), "Diferencia", "+$${fmt(diferencia)}", OrangeWarn, OrangeBg)
            ResumenCard(
                Modifier.weight(1f),
                "Discrepancias",
                "${crucesMock.count { it.estado != EstadoCruce.OK }} cruces",
                RedAlert,
                RedBg,
            )
        }
    }
}

@Composable
private fun ResumenCard(modifier: Modifier, label: String, valor: String, color: Color, bg: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(valor, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        }
    }
}

// ─── 3 · Por concesionaria ────────────────────────────────────────────────────
@Composable
private fun ConcesionariasSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
        SectionTitle("Por concesionaria")
        Spacer(Modifier.height(10.dp))
        concesionariasMock.forEach { c ->
            ConcesionariaCard(c)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ConcesionariaCard(c: ResumenConcesionaria) {
    val diferencia = c.boleta - c.tagOk
    val (bgColor, badgeColor, badgeLabel) = when {
        diferencia == 0 -> Triple(GreenBg,   GreenOk,    "Sin diferencia")
        diferencia > 0  -> Triple(OrangeBg,  OrangeWarn, "+$${fmt(diferencia)}")
        else            -> Triple(Color.White, TextSecondary, "-$${fmt(-diferencia)}")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        border = if (diferencia != 0) BorderStroke(1.dp, badgeColor.copy(alpha = 0.25f)) else null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Inicial de la concesionaria
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(InputBackground),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = c.nombre.first().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Blue40,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(c.nombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniLabel("TAG OK", "$${fmt(c.tagOk)}", Blue40)
                    MiniLabel("Boleta", "$${fmt(c.boleta)}", if (diferencia > 0) RedAlert else TextDark)
                }
            }
            Spacer(Modifier.width(8.dp))
            EstadoBadge(badgeLabel, badgeColor)
        }
    }
}

@Composable
private fun MiniLabel(label: String, valor: String, color: Color) {
    Column {
        Text(label, fontSize = 10.sp, color = TextSecondary)
        Text(valor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = color)
    }
}

// ─── 4 · Tendencia mensual ────────────────────────────────────────────────────
@Composable
private fun TendenciaSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Tendencia últimos 6 meses")
            Spacer(Modifier.height(6.dp))

            // Leyenda
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LegendaDot(Blue40,    "TAG OK")
                LegendaDot(RedAlert,  "Concesionaria")
            }
            Spacer(Modifier.height(16.dp))

            // Barras
            val maxVal = tendenciaMock.maxOf { maxOf(it.tagOk, it.boleta) }.toFloat()
            val barHeight = 100.dp

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                tendenciaMock.forEach { mes ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        // Ambas barras del mes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(barHeight),
                        ) {
                            // Barra TAG OK
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(barHeight * (mes.tagOk / maxVal))
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(Blue40),
                            )
                            // Barra concesionaria
                            Box(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(barHeight * (mes.boleta / maxVal))
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        if (mes.boleta > mes.tagOk) RedAlert
                                        else GreenOk
                                    ),
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(mes.mes, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerGray)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Mayo registra la mayor discrepancia de los últimos 6 meses",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun LegendaDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}

// ─── 5 · Importar boleta ──────────────────────────────────────────────────────
@Composable
private fun ImportarBoleta(estado: EstadoBoleta, onImportar: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        AnimatedContent(
            targetState = estado,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "boleta_state",
        ) { s ->
            when (s) {
                EstadoBoleta.NINGUNA -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier.size(56.dp).clip(CircleShape).background(InputBackground),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.CloudUpload, null, tint = Blue40, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Importar boleta de concesionaria", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextDark)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Sube el PDF de tu boleta mensual.\nLa IA extraerá los cobros automáticamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onImportar,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue40),
                        ) {
                            Icon(Icons.Filled.CloudUpload, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Subir boleta (PDF)", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                EstadoBoleta.ANALIZANDO -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(color = Blue40, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "IA analizando tu boleta...",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            modifier = Modifier.alpha(pulse),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Extrayendo cobros y comparando con tu historial",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                EstadoBoleta.ANALIZADA -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.CheckCircle, null, tint = GreenOk, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Boleta analizada correctamente", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
                            Text("Autopista Central · Costanera Norte · Vespucio Sur", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// ─── 6 · Insight IA ───────────────────────────────────────────────────────────
@Composable
private fun InsightIACard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.AutoAwesome, null, tint = Blue40, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Análisis de IA", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Blue40)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Se detectaron 2 discrepancias en tu boleta de mayo. " +
                        "Costanera Norte cobró \$200 adicionales el 3 de mayo sin justificación tarifaria. " +
                        "Además, hay un cobro del 14 de mayo en Autopista Central sin cruce registrado — " +
                        "posible error de lectura de TAG o cobro indebido. " +
                        "Se recomienda contactar a las concesionarias con este reporte como respaldo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1E3A8A),
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

// ─── 7 · Detalle de cruces ────────────────────────────────────────────────────
private data class EstiloEstado(
    val bgColor: Color,
    val iconColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val etiqueta: String,
)

@Composable
private fun CruceRow(cruce: CruceComparado) {
    val estilo = when (cruce.estado) {
        EstadoCruce.OK           -> EstiloEstado(Color.White, GreenOk,    Icons.Filled.CheckCircle, "OK")
        EstadoCruce.DISCREPANCIA -> EstiloEstado(RedBg,       RedAlert,   Icons.Filled.Warning,     "Discrepancia")
        EstadoCruce.SIN_REGISTRO -> EstiloEstado(OrangeBg,    OrangeWarn, Icons.Filled.Info,        "Sin registro")
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = estilo.bgColor),
        elevation = CardDefaults.cardElevation(if (cruce.estado == EstadoCruce.OK) 0.dp else 1.dp),
        border = if (cruce.estado != EstadoCruce.OK) BorderStroke(1.dp, estilo.iconColor.copy(alpha = 0.25f)) else null,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(estilo.icon, null, tint = estilo.iconColor, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(cruce.fecha, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                }
                EstadoBadge(estilo.etiqueta, estilo.iconColor)
            }
            Spacer(Modifier.height(6.dp))
            Text(cruce.portico, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
            Text(cruce.concesionaria, fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = DividerGray)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                MontoCol(
                    Modifier.weight(1f),
                    "TAG OK registró",
                    cruce.montoTagOk?.let { "$${fmt(it)}" } ?: "—",
                    if (cruce.montoTagOk == null) TextSecondary else TextDark,
                )
                MontoCol(
                    Modifier.weight(1f),
                    "Concesionaria cobró",
                    cruce.montoBoleta?.let { "$${fmt(it)}" } ?: "—",
                    when (cruce.estado) {
                        EstadoCruce.DISCREPANCIA, EstadoCruce.SIN_REGISTRO -> estilo.iconColor
                        else -> TextDark
                    },
                )
            }
        }
    }
}

// ─── Componentes reutilizables ────────────────────────────────────────────────
@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = TextDark,
        modifier = modifier,
    )
}

@Composable
private fun MontoCol(modifier: Modifier, label: String, monto: String, color: Color) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Spacer(Modifier.height(2.dp))
        Text(monto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
    }
}

@Composable
private fun EstadoBadge(etiqueta: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(etiqueta, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}

private fun fmt(n: Int): String = "%,d".format(n)
