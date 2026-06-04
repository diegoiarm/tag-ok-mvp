// ui/historial/components/shared/FilterChips.kt
package com.tagok.app.ui.historial.components.shared

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tagok.app.ui.historial.model.PatenteFilter
import com.tagok.app.ui.historial.model.SortOption
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    currentSort: SortOption,
    patentes: List<PatenteFilter>,
    onSortSelected: (SortOption) -> Unit,
    onPatenteToggle: (String) -> Unit,
    onClearPatentes: () -> Unit,
    modifier: Modifier = Modifier)
{
    var expandedSort by remember { mutableStateOf(false) }
    var expandedPatentes by remember { mutableStateOf(false) }

    val patentesSeleccionadas = patentes.filter { it.isSelected }

    Column(modifier = modifier.fillMaxWidth())
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp))
        {
            Box {
                FilterChip(
                    selected = true,
                    onClick = { expandedSort = true },
                    label = { Text(currentSort.displayName) },
                    leadingIcon = {
                        Icon(
                            imageVector = getSortIcon(currentSort),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp))
                    },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = "Cambiar orden",
                            modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Blue40.copy(alpha = 0.15f),
                        selectedLabelColor = Blue40,
                        selectedLeadingIconColor = Blue40,
                        selectedTrailingIconColor = Blue40))

                DropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false })
                {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically)
                                {
                                    Text(
                                        text = option.displayName,
                                        style = MaterialTheme.typography.bodyMedium)
                                    if (option == currentSort)
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = "Seleccionado",
                                            tint = Blue40,
                                            modifier = Modifier.size(18.dp))
                                }
                            },
                            onClick = {
                                onSortSelected(option)
                                expandedSort = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = getSortIcon(option),
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp))
                            })
                    }
                }
            }

            Box {
                FilterChip(
                    selected = patentesSeleccionadas.isNotEmpty(),
                    onClick = { expandedPatentes = true },
                    label = {
                        Text(
                            text = when
                            {
                                patentesSeleccionadas.isEmpty() -> "Patentes"
                                patentesSeleccionadas.size == 1 -> patentesSeleccionadas.first().patente  // ← .patente
                                else -> "${patentesSeleccionadas.size} patentes"
                            })
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp))
                    },
                    trailingIcon = {
                        if (patentesSeleccionadas.isNotEmpty())
                        {
                            IconButton(
                                onClick = {
                                    onClearPatentes()
                                    expandedPatentes = false
                                },
                                modifier = Modifier.size(16.dp))
                            {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Limpiar filtro",
                                    modifier = Modifier.size(14.dp))
                            }
                        }
                        else
                        {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Ver patentes",
                                modifier = Modifier.size(16.dp))
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Blue40.copy(alpha = 0.15f),
                        selectedLabelColor = Blue40,
                        selectedLeadingIconColor = Blue40)
                )

                DropdownMenu(
                    expanded = expandedPatentes,
                    onDismissRequest = { expandedPatentes = false })
                {
                    if (patentes.isEmpty())
                    {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "No hay patentes disponibles",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary)
                            },
                            onClick = { expandedPatentes = false },
                            enabled = false)
                    }
                    else
                    {
                        patentes.forEach { patenteFilter ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically)
                                    {
                                        Text(
                                            text = patenteFilter.patente,
                                            style = MaterialTheme.typography.bodyMedium)
                                        if (patenteFilter.isSelected)
                                        {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = "Seleccionada",
                                                tint = Blue40,
                                                modifier = Modifier.size(18.dp))
                                        }
                                    }
                                },
                                onClick = {
                                    onPatenteToggle(patenteFilter.patente)
                                },
                                leadingIcon = {
                                    Checkbox(
                                        checked = patenteFilter.isSelected,
                                        onCheckedChange = { onPatenteToggle(patenteFilter.patente) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Blue40))
                                })
                        }

                        if (patentesSeleccionadas.isNotEmpty())
                        {
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Limpiar filtros",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error)
                                },
                                onClick = {
                                    onClearPatentes()
                                    expandedPatentes = false
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Clear,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp))
                                })
                        }
                    }
                }
            }
        }
    }
}

fun getSortIcon(sort: SortOption): ImageVector
{
    return when (sort)
    {
        SortOption.MOST_CRUCES -> Icons.Filled.TrendingUp
        SortOption.LEAST_CRUCES -> Icons.Filled.TrendingDown
        SortOption.HIGHEST_AMOUNT -> Icons.Filled.ArrowUpward
        SortOption.LOWEST_AMOUNT -> Icons.Filled.ArrowDownward
        SortOption.NEWEST -> Icons.Filled.Schedule
        SortOption.OLDEST -> Icons.Filled.History
        SortOption.DEFAULT -> Icons.Filled.Sort
    }
}