package com.tagok.app.ui.boleta.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary
import kotlinx.datetime.LocalDate
import java.time.ZoneId

@Composable
fun PatenteSelector(
    patentes: List<String>,
    selected: String,
    onPatenteSelected: (String) -> Unit)
{
    Column {
        Text(
            text = "Patente del vehículo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp))

        if (patentes.isEmpty())
        {
            Text(
                text = "No hay patentes disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary)
        }
        else
        {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp))
            {
                items(patentes) { patente ->
                    FilterChip(
                        selected = patente == selected,
                        onClick = { onPatenteSelected(patente) },
                        label = { Text(patente) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue40,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary))
                }
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

    Column {
        Text(
            text = "Rango de fechas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp))
        {
            // Fecha Desde
            OutlinedCard(
                modifier = Modifier
                    .weight(1f),
                onClick = { showDesdePicker = true })
            {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally)
                {
                    Text(
                        text = "Desde",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = formatDate(fechaDesde),
                        style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Fecha Hasta
            OutlinedCard(
                modifier = Modifier
                    .weight(1f),
                onClick = { showHastaPicker = true })
            {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        text = "Hasta",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = formatDate(fechaHasta),
                        style = MaterialTheme.typography.bodyLarge)
                }
            }
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
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDesdePicker = false }) {
                        Text("Cancelar")
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
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showHastaPicker = false }) {
                        Text("Cancelar")
                    }
                })
            {
                DatePicker(state = datePickerState)
            }
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
    Column {
        Text(
            text = "Autopistas (opcional - dejar vacío para todas)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp))

        if (autopistas.isEmpty())
        {
            Text(
                text = "No hay autopistas disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary)
        } else
        {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp))
            {
                items(autopistas) { autopista ->
                    FilterChip(
                        selected = autopista in selected,
                        onClick = { onToggle(autopista) },
                        label = { Text(autopista) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Blue40,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary))
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
                    Text("Limpiar selección")
                }
            }
        }
    }
}