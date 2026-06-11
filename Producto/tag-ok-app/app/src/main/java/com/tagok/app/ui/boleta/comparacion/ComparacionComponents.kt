package com.tagok.app.ui.boleta.comparacion

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Description
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.boleta.ComparacionFactura
import com.tagok.app.domain.model.boleta.ComparacionItem
import com.tagok.app.domain.model.boleta.EstadoComparacion
import com.tagok.app.ui.historial.utils.formatCurrency
import kotlinx.datetime.LocalDate

// Paleta semántica del veredicto (no existe en el colorScheme del tema)
private val VerdeOk = Color(0xFF1B873B)
private val VerdeOkContenedor = Color(0xFFE7F6EC)
private val AmbarAviso = Color(0xFF9A6700)
private val AmbarAvisoContenedor = Color(0xFFFFF4CE)

internal fun LocalDate.formatear(): String =
    "%02d-%02d-%04d".format(dayOfMonth, monthNumber, year)

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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp))
    {
        item { VeredictoCard(resultado) }
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
            items(resultado.diferencias) { item ->
                DiferenciaRow(item)
            }
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

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp))
            {
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
    val fecha = item.itemApp?.fecha?.formatear() ?: item.itemFactura?.fecha ?: ""

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
