package com.tagok.app.ui.boleta.comparacion

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.boleta.ComparacionFactura
import com.tagok.app.domain.model.boleta.ComparacionItem
import com.tagok.app.domain.model.boleta.EstadoComparacion
import com.tagok.app.ui.historial.utils.formatCurrency
import kotlin.math.abs
import kotlinx.datetime.LocalDate

// Paleta semántica del veredicto (no existe en el colorScheme del tema)
private val VerdeOk = Color(0xFF1B873B)
private val VerdeOkContenedor = Color(0xFFE7F6EC)
private val AmbarAviso = Color(0xFF9A6700)
private val AmbarAvisoContenedor = Color(0xFFFFF4CE)

internal fun LocalDate.formatear(): String =
    "%02d-%02d-%04d".format(dayOfMonth, monthNumber, year)

// La IA devuelve la fecha como texto ISO (yyyy-MM-dd); si no se puede parsear
// se muestra tal cual vino en la factura.
private fun String.formatearFechaIso(): String =
    runCatching { LocalDate.parse(this).formatear() }.getOrDefault(this)

// ---------------------------------------------------------------------------
// Paso 1: selección de archivo
// ---------------------------------------------------------------------------

@Composable
internal fun ContextoBoletaCard(
    patente: String,
    fechaDesde: LocalDate,
    fechaHasta: LocalDate,
    autopistas: List<String>)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)))
    {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            Icon(
                Icons.Outlined.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Boleta TAG OK · $patente",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold)
                Text(
                    text = "${fechaDesde.formatear()} al ${fechaHasta.formatear()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = if (autopistas.isEmpty()) "Todas las autopistas"
                        else autopistas.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
internal fun OpcionAdjuntoRow(
    icon: ImageVector,
    titulo: String,
    subtitulo: String,
    enabled: Boolean,
    onClick: () -> Unit)
{
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp))
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
internal fun ArchivoSeleccionadoCard(
    archivo: com.tagok.app.domain.model.boleta.ArchivoFactura,
    onQuitar: () -> Unit)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            Icon(
                imageVector = if (archivo.esPdf) Icons.Outlined.PictureAsPdf else Icons.Outlined.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp))

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f))
            {
                Text(
                    text = archivo.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Text(
                    text = "${if (archivo.esPdf) "PDF" else "Imagen"} · ${archivo.tamanoLegible}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = onQuitar)
            {
                Icon(Icons.Default.Close, contentDescription = "Quitar archivo")
            }
        }
    }
}

@Composable
internal fun AnalizandoOverlay()
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
                    text = "Leyendo la factura con IA...",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Esto puede tardar hasta un minuto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Paso 2: resultado de la comparación
// ---------------------------------------------------------------------------

@Composable
internal fun ResultadoComparacionContent(
    resultado: ComparacionFactura,
    onNuevaComparacion: () -> Unit)
{
    var coincidenciasExpandidas by remember { mutableStateOf(false) }
    var mostrarGuiaReclamo by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val hayDiferencias = resultado.diferencias.isNotEmpty()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp))
    {
        item { VeredictoCard(resultado) }
        item { ResumenChips(resultado) }
        item { TotalesCard(resultado) }

        if (resultado.diferencias.isNotEmpty())
        {
            item {
                Text(
                    text = "Diferencias (${resultado.diferencias.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp))
            }

            seccionDiferencias(
                titulo = "Cobros de más en la factura",
                diferencias = resultado.diferencias
                    .filter { it.estado == EstadoComparacion.SOLO_EN_FACTURA }
                    .sortedByDescending { it.diferenciaValor })
            seccionDiferencias(
                titulo = "Montos distintos",
                diferencias = resultado.diferencias
                    .filter { it.estado == EstadoComparacion.MONTO_DIFERENTE }
                    .sortedByDescending { abs(it.diferenciaValor) })
            seccionDiferencias(
                titulo = "No aparecen en tu factura",
                diferencias = resultado.diferencias
                    .filter { it.estado == EstadoComparacion.SOLO_EN_APP }
                    .sortedBy { it.diferenciaValor })
        }

        if (resultado.itemsCoincidentes.isNotEmpty())
        {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { coincidenciasExpandidas = !coincidenciasExpandidas }
                        .padding(top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Text(
                        text = "Coincidencias (${resultado.itemsCoincidentes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.ExpandMore,
                        contentDescription = if (coincidenciasExpandidas) "Contraer" else "Expandir",
                        modifier = Modifier.rotate(if (coincidenciasExpandidas) 180f else 0f))
                }
            }

            if (coincidenciasExpandidas)
            {
                items(resultado.itemsCoincidentes) { item ->
                    CoincidenciaRow(item)
                }
            }
        }

        if (hayDiferencias)
        {
            item { GuiaReclamoCard(onVerPasos = { mostrarGuiaReclamo = true }) }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp))
            {
                Button(
                    onClick = { compartirInforme(context, construirInformeReclamo(resultado)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium)
                {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartir informe")
                }

                Text(
                    text = "Lectura realizada con IA: puede contener errores. " +
                        "Ante diferencias, verifica con tu concesionaria.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline)

                OutlinedButton(
                    onClick = onNuevaComparacion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium)
                {
                    Text("Comparar otra factura")
                }
            }
        }
    }

    if (mostrarGuiaReclamo)
    {
        GuiaReclamoDialog(onDismiss = { mostrarGuiaReclamo = false })
    }
}

// ---------------------------------------------------------------------------
// Guía de reclamo ante discrepancias + compartir informe
// ---------------------------------------------------------------------------

@Composable
private fun GuiaReclamoCard(onVerPasos: () -> Unit)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AmbarAvisoContenedor))
    {
        Column(modifier = Modifier.padding(16.dp))
        {
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Icon(
                    Icons.Outlined.Gavel,
                    contentDescription = null,
                    tint = AmbarAviso,
                    modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "¿Cobros que no reconoces?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AmbarAviso)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Si la factura tiene cobros indebidos, duplicados o injustificados, " +
                    "tienes un camino para reclamar. Conoce los pasos.",
                style = MaterialTheme.typography.bodySmall,
                color = AmbarAviso.copy(alpha = 0.9f))

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onVerPasos,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmbarAviso,
                    contentColor = Color.White))
            {
                Text("Cómo reclamar paso a paso")
            }
        }
    }
}

private data class PasoReclamo(val titulo: String, val descripcion: String)

private val PASOS_RECLAMO = listOf(
    PasoReclamo(
        "1. Concesionaria de la autopista",
        "Es el primer paso. Si desconoces un traslado, hay cobros duplicados o el monto no " +
            "corresponde, ingresa tu reclamo en la oficina virtual o sucursales de la autopista " +
            "involucrada (por ejemplo, Autopase)."),
    PasoReclamo(
        "2. SERNAC",
        "Si la autopista no entrega una respuesta satisfactoria, presenta tu reclamo en el Portal " +
            "del Consumidor con tu ClaveÚnica. El SERNAC puede exigir a la empresa corregir cobros " +
            "erróneos, duplicados o injustificados, amparado en la Ley del Consumidor."),
    PasoReclamo(
        "3. Ministerio de Obras Públicas (MOP)",
        "Para problemas del sistema de tarificación de las autopistas concesionadas, deja una " +
            "constancia formal en la Plataforma de Atención Ciudadana MOP o en la Dirección General " +
            "de Concesiones, que fiscaliza el cumplimiento de los contratos."),
    PasoReclamo(
        "4. Instancia judicial",
        "Si los cobros provienen de clonación de patentes, robos o montos elevados que la empresa " +
            "insiste en mantener, puedes presentar denuncia y demanda civil en el Juzgado de Policía " +
            "Local para exigir la anulación de la deuda e indemnizaciones. Existe asesoría legal " +
            "especializada en deudas asociadas al TAG."))

@Composable
private fun GuiaReclamoDialog(onDismiss: () -> Unit)
{
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Gavel, contentDescription = null, tint = AmbarAviso) },
        title = {
            Text(
                text = "Cómo reclamar",
                fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp))
            {
                Text(
                    text = "Los reclamos por cobros desmedidos, indebidos o duplicados en el TAG se " +
                        "presentan siguiendo esta ruta, según la naturaleza del problema:",
                    style = MaterialTheme.typography.bodyMedium)

                PASOS_RECLAMO.forEach { paso ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp))
                    {
                        Text(
                            text = paso.titulo,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = paso.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(
                    text = "Esta guía es orientativa y no constituye asesoría legal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Entendido") }
        })
}

private fun construirInformeReclamo(resultado: ComparacionFactura): String
{
    val boleta = resultado.boletaApp
    val sb = StringBuilder()
    sb.appendLine("INFORME DE VERIFICACIÓN — TAG OK")
    sb.appendLine("Patente: ${boleta.patente}")
    sb.appendLine("Período: ${boleta.fechaDesde.formatear()} al ${boleta.fechaHasta.formatear()}")
    sb.appendLine()
    sb.appendLine("RESUMEN")
    sb.appendLine("Boleta TAG OK: ${resultado.totalApp.formatCurrency()}")
    sb.appendLine("Factura concesionaria: ${resultado.totalFactura.formatCurrency()}")
    val signo = if (resultado.diferenciaTotal > 0) "+" else ""
    sb.appendLine("Diferencia: $signo${resultado.diferenciaTotal.formatCurrency()}")
    sb.appendLine()

    if (resultado.diferencias.isEmpty())
    {
        sb.appendLine("Sin diferencias: la factura coincide con lo registrado por TAG OK.")
    }
    else
    {
        sb.appendLine("DIFERENCIAS DETECTADAS (${resultado.diferencias.size})")
        resultado.diferencias.forEach { item ->
            val nombre = item.itemApp?.nombre ?: item.itemFactura?.portico ?: "Cruce sin identificar"
            val fecha = item.itemApp?.fecha?.formatear()
                ?: item.itemFactura?.fecha?.formatearFechaIso()
                ?: "fecha no disponible"
            val detalle = when (item.estado)
            {
                EstadoComparacion.MONTO_DIFERENTE ->
                    "monto distinto — TAG OK ${item.itemApp?.valor?.formatCurrency() ?: "—"} " +
                        "vs factura ${item.itemFactura?.valor?.formatCurrency() ?: "—"}"
                EstadoComparacion.SOLO_EN_FACTURA ->
                    "cobrado en la factura pero no registrado en TAG OK " +
                        "(${item.itemFactura?.valor?.formatCurrency() ?: "—"})"
                EstadoComparacion.SOLO_EN_APP ->
                    "registrado en TAG OK pero ausente en la factura " +
                        "(${item.itemApp?.valor?.formatCurrency() ?: "—"})"
                EstadoComparacion.COINCIDE -> ""
            }
            sb.appendLine("• $nombre ($fecha): $detalle")
        }
    }

    sb.appendLine()
    sb.appendLine("Generado con TAG OK. La lectura de la factura se realizó con IA y puede " +
        "contener errores; verifica siempre con tu concesionaria.")
    return sb.toString()
}

private fun compartirInforme(context: Context, texto: String)
{
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Informe de verificación TAG OK")
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir informe"))
}

private fun LazyListScope.seccionDiferencias(
    titulo: String,
    diferencias: List<ComparacionItem>)
{
    if (diferencias.isEmpty()) return

    item {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp))
    }
    items(diferencias) { item ->
        DiferenciaRow(item)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResumenChips(resultado: ComparacionFactura)
{
    val montosDistintos = resultado.diferencias
        .count { it.estado == EstadoComparacion.MONTO_DIFERENTE }
    val soloEnFactura = resultado.diferencias
        .count { it.estado == EstadoComparacion.SOLO_EN_FACTURA }
    val soloEnApp = resultado.diferencias
        .count { it.estado == EstadoComparacion.SOLO_EN_APP }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp))
    {
        if (resultado.coincidencias > 0)
        {
            ResumenChip(
                texto = if (resultado.coincidencias == 1) "1 coincide"
                    else "${resultado.coincidencias} coinciden",
                color = VerdeOk)
        }
        if (soloEnFactura > 0)
        {
            ResumenChip(
                texto = "$soloEnFactura solo en factura",
                color = MaterialTheme.colorScheme.error)
        }
        if (montosDistintos > 0)
        {
            ResumenChip(
                texto = if (montosDistintos == 1) "1 monto distinto"
                    else "$montosDistintos montos distintos",
                color = AmbarAviso)
        }
        if (soloEnApp > 0)
        {
            ResumenChip(
                texto = "$soloEnApp solo en TAG OK",
                color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun ResumenChip(texto: String, color: Color)
{
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp))
    {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
    }
}

@Composable
private fun VeredictoCard(resultado: ComparacionFactura)
{
    val (contenedor, contenido, icono, titulo, subtitulo) = if (resultado.cuadra)
    {
        Veredicto(
            VerdeOkContenedor, VerdeOk, Icons.Default.CheckCircle,
            "La factura cuadra",
            "Los cruces y montos coinciden con lo registrado por TAG OK")
    }
    else
    {
        Veredicto(
            AmbarAvisoContenedor, AmbarAviso, Icons.Default.WarningAmber,
            if (resultado.diferencias.size == 1) "1 diferencia encontrada"
                else "${resultado.diferencias.size} diferencias encontradas",
            "Revisa el detalle antes de pagar la factura")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = contenedor))
    {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = contenido,
                modifier = Modifier.size(36.dp))

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contenido)
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = contenido.copy(alpha = 0.8f))
            }
        }
    }
}

private data class Veredicto(
    val contenedor: Color,
    val contenido: Color,
    val icono: ImageVector,
    val titulo: String,
    val subtitulo: String)

@Composable
private fun TotalesCard(resultado: ComparacionFactura)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)))
    {
        Column(modifier = Modifier.padding(16.dp))
        {
            Row(modifier = Modifier.fillMaxWidth())
            {
                TotalCelda(
                    etiqueta = "Boleta TAG OK",
                    valor = resultado.totalApp.formatCurrency(),
                    modifier = Modifier.weight(1f))
                TotalCelda(
                    etiqueta = "Factura",
                    valor = resultado.totalFactura.formatCurrency(),
                    modifier = Modifier.weight(1f))
                TotalCelda(
                    etiqueta = "Diferencia",
                    valor = (if (resultado.diferenciaTotal > 0) "+" else "") +
                        resultado.diferenciaTotal.formatCurrency(),
                    valorColor = when
                    {
                        resultado.diferenciaTotal > 0 -> MaterialTheme.colorScheme.error
                        resultado.diferenciaTotal < 0 -> VerdeOk
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.weight(1f))
            }

            if (resultado.diferenciaTotal != 0.0)
            {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (resultado.diferenciaTotal > 0)
                        "La factura cobra ${resultado.diferenciaTotal.formatCurrency()} más que lo registrado en TAG OK"
                    else
                        "La factura cobra ${(-resultado.diferenciaTotal).formatCurrency()} menos que lo registrado en TAG OK",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TotalCelda(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    valorColor: Color = MaterialTheme.colorScheme.onSurface)
{
    Column(modifier = modifier)
    {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = valor,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = valorColor)
    }
}

@Composable
private fun DiferenciaRow(item: ComparacionItem)
{
    val (etiqueta, etiquetaColor) = when (item.estado)
    {
        EstadoComparacion.MONTO_DIFERENTE -> "Monto distinto" to AmbarAviso
        EstadoComparacion.SOLO_EN_FACTURA -> "No registrado en TAG OK" to MaterialTheme.colorScheme.error
        EstadoComparacion.SOLO_EN_APP -> "No aparece en la factura" to MaterialTheme.colorScheme.outline
        EstadoComparacion.COINCIDE -> "" to MaterialTheme.colorScheme.outline
    }

    val nombre = item.itemApp?.nombre ?: item.itemFactura?.portico ?: "Cruce sin identificar"
    val fecha = item.itemApp?.fecha?.formatear()
        ?: item.itemFactura?.fecha?.formatearFechaIso()
        ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp))
    {
        Column(modifier = Modifier.padding(16.dp))
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically)
            {
                Column(modifier = Modifier.weight(1f))
                {
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    if (fecha.isNotBlank())
                    {
                        Text(
                            text = fecha,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    color = etiquetaColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp))
                {
                    Text(
                        text = etiqueta,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = etiquetaColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (item.estado)
            {
                EstadoComparacion.MONTO_DIFERENTE ->
                {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp))
                    {
                        MontoDetalle("TAG OK", item.itemApp?.valor)
                        MontoDetalle("Factura", item.itemFactura?.valor)
                        MontoDetalle(
                            "Diferencia",
                            null,
                            textoOverride = (if (item.diferenciaValor > 0) "+" else "") +
                                item.diferenciaValor.formatCurrency(),
                            color = if (item.diferenciaValor > 0)
                                MaterialTheme.colorScheme.error else VerdeOk)
                    }
                }

                EstadoComparacion.SOLO_EN_FACTURA ->
                    MontoDetalle("Cobrado en factura", item.itemFactura?.valor)

                EstadoComparacion.SOLO_EN_APP ->
                    MontoDetalle("Registrado en TAG OK", item.itemApp?.valor)

                EstadoComparacion.COINCIDE -> {}
            }
        }
    }
}

@Composable
private fun MontoDetalle(
    etiqueta: String,
    valor: Double?,
    textoOverride: String? = null,
    color: Color = MaterialTheme.colorScheme.onSurface)
{
    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = textoOverride ?: valor?.formatCurrency() ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color)
    }
}

@Composable
private fun CoincidenciaRow(item: ComparacionItem)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically)
    {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = VerdeOk,
            modifier = Modifier.size(18.dp))

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f))
        {
            Text(
                text = item.itemApp?.nombre ?: item.itemFactura?.portico ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Text(
                text = item.itemApp?.fecha?.formatear() ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            text = item.itemApp?.valor?.formatCurrency() ?: "",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold)
    }
}
