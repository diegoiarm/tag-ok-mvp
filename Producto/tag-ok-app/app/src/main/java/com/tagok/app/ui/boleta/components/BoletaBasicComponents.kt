package com.tagok.app.ui.boleta.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.ui.theme.AccentBlue
import com.tagok.app.ui.theme.DividerGray
import com.tagok.app.ui.theme.NavyBlue
import com.tagok.app.ui.theme.TextDark
import kotlinx.datetime.LocalDate
import java.time.ZoneId

@Composable
fun PatenteSelector(
    vehiculos: List<Vehiculo>,
    selected: String,
    onPatenteSelected: (String) -> Unit)
{
    if (vehiculos.isEmpty())
    {
        Text(
            text = "No hay vehículos registrados",
            fontSize = 13.sp,
            color = androidx.compose.ui.graphics.Color.Gray)
    }
    else
    {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp))
        {
            items(vehiculos) { vehiculo ->
                BrandChip(
                    label = vehiculo.patente,
                    selected = vehiculo.patente == selected,
                    onClick = { onPatenteSelected(vehiculo.patente) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    fechaDesde: LocalDate,
    fechaHasta: LocalDate,
    onDesdeChanged: (LocalDate) -> Unit,
    onHastaChanged: (LocalDate) -> Unit)
{
    var showDesdePicker by remember { mutableStateOf(false) }
    var showHastaPicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp))
    {
        DateBox(label = "Desde", value = formatDate(fechaDesde), modifier = Modifier.weight(1f), onClick = { showDesdePicker = true })
        DateBox(label = "Hasta", value = formatDate(fechaHasta), modifier = Modifier.weight(1f), onClick = { showHastaPicker = true })
    }

    // DatePickerDialog para Desde
    if (showDesdePicker)
    {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = localDateToMillis(fechaDesde))

        DatePickerDialog(
            onDismissRequest = { showDesdePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDesdeChanged(millisToLocalDate(millis))
                        }
                        showDesdePicker = false
                    })
                {
                    Text("OK", color = AccentBlue, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDesdePicker = false }) {
                    Text("Cancelar", color = androidx.compose.ui.graphics.Color.Gray)
                }
            })
        {
            DatePicker(state = datePickerState)
        }
    }

    // DatePickerDialog para Hasta
    if (showHastaPicker)
    {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = localDateToMillis(fechaHasta))

        DatePickerDialog(
            onDismissRequest = { showHastaPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onHastaChanged(millisToLocalDate(millis))
                        }
                        showHastaPicker = false
                    })
                {
                    Text("OK", color = AccentBlue, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHastaPicker = false }) {
                    Text("Cancelar", color = androidx.compose.ui.graphics.Color.Gray)
                }
            })
        {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun DateBox(label: String, value: String, modifier: Modifier = Modifier, onClick: () -> Unit)
{
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerGray))
    {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentBlue, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
        }
    }
}

private fun formatDate(date: LocalDate): String
{
    return "${date.dayOfMonth.toString().padStart(2, '0')}/" +
            "${date.monthNumber.toString().padStart(2, '0')}/" +
            "${date.year}"
}

private fun localDateToMillis(localDate: LocalDate): Long
{
    return java.time.LocalDate.of(
        localDate.year,
        localDate.monthNumber,
        localDate.dayOfMonth
    ).atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun millisToLocalDate(millis: Long): LocalDate
{
    val instant = java.time.Instant.ofEpochMilli(millis)
    val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
    return LocalDate(
        year = localDateTime.year,
        monthNumber = localDateTime.monthValue,
        dayOfMonth = localDateTime.dayOfMonth
    )
}

@Composable
fun AutopistaMultiSelect(
    autopistas: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit)
{
    if (autopistas.isEmpty())
    {
        Text(
            text = "No hay autopistas disponibles",
            fontSize = 13.sp,
            color = androidx.compose.ui.graphics.Color.Gray)
    } else
    {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp))
        {
            items(autopistas) { autopista ->
                BrandChip(
                    label = autopista,
                    selected = autopista in selected,
                    onClick = { onToggle(autopista) })
            }
        }

        if (selected.isNotEmpty())
        {
            TextButton(
                onClick = {
                    // Limpiar todas las selecciones
                    selected.forEach { onToggle(it) }
                })
            {
                Text("Limpiar selección", color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun BrandChip(label: String, selected: Boolean, onClick: () -> Unit)
{
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = NavyBlue,
            selectedLabelColor = androidx.compose.ui.graphics.Color.White,
            containerColor = androidx.compose.ui.graphics.Color.White,
            labelColor = androidx.compose.ui.graphics.Color.Gray),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = DividerGray,
            selectedBorderColor = NavyBlue))
}
