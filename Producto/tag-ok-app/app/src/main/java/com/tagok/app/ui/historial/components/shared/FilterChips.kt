// ui/historial/components/shared/FilterChips.kt
package com.tagok.app.ui.historial.components.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.tagok.app.ui.historial.model.AutopistaFilter
import com.tagok.app.ui.historial.model.PatenteFilter
import com.tagok.app.ui.historial.model.SortOption
import com.tagok.app.ui.theme.AccentBlue
import com.tagok.app.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    currentSort: SortOption,
    patentes: List<PatenteFilter>,
    autopistas: List<AutopistaFilter>,
    onSortSelected: (SortOption) -> Unit,
    onPatenteToggle: (String) -> Unit,
    onAutopistaToggle: (String) -> Unit,
    onClearPatentes: () -> Unit,
    onClearAutopistas: () -> Unit,
    onApplyFilters: () -> Unit,
    modifier: Modifier = Modifier)
{
    var showFilterModal by remember { mutableStateOf(false) }

    val patentesSeleccionadas = patentes.filter { it.isSelected }
    val autopistasSeleccionadas = autopistas.filter { it.isSelected }
    val tieneFiltrosActivos = patentesSeleccionadas.isNotEmpty() || autopistasSeleccionadas.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth())
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            SortChip(
                currentSort = currentSort,
                onSortSelected = onSortSelected)

            FilterChip(
                selected = tieneFiltrosActivos,
                onClick = { showFilterModal = true },
                label = {
                    val texto = buildString {
                        if (patentesSeleccionadas.isNotEmpty())
                        {
                            append("${patentesSeleccionadas.size} pat.")
                        }
                        if (autopistasSeleccionadas.isNotEmpty())
                        {
                            if (isNotEmpty()) append(" • ")
                            append("${autopistasSeleccionadas.size} autop.")
                        }
                        if (isEmpty()) append("Filtros")
                    }
                    Text(texto)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (tieneFiltrosActivos)
                    {
                        IconButton(
                            onClick = {
                                onClearPatentes()
                                onClearAutopistas()
                            },
                            modifier = Modifier.size(16.dp))
                        {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Limpiar filtros",
                                modifier = Modifier.size(14.dp))
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NavyBlue,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White,
                    selectedTrailingIconColor = Color.White))
        }
    }

    if (showFilterModal)
    {
        FilterModal(
            patentes = patentes,
            autopistas = autopistas,
            patentesSeleccionadas = patentesSeleccionadas,
            autopistasSeleccionadas = autopistasSeleccionadas,
            onPatenteToggle = onPatenteToggle,
            onAutopistaToggle = onAutopistaToggle,
            onClearPatentes = onClearPatentes,
            onClearAutopistas = onClearAutopistas,
            onDismiss = {
                showFilterModal = false
                onApplyFilters()
            })
    }
}

// ── Chip de Ordenamiento (Compacto) ───────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortChip(
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = true,
            onClick = { expanded = true },
            label = { Text(currentSort.displayName) },
            leadingIcon = {
                Icon(
                    imageVector = getSortIcon(currentSort),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Cambiar orden",
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = NavyBlue,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White,
                selectedTrailingIconColor = Color.White
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (option == currentSort) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Seleccionado",
                                    tint = NavyBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        onSortSelected(option)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = getSortIcon(option),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
        }
    }
}

// ── Modal de Filtros ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterModal(
    patentes: List<PatenteFilter>,
    autopistas: List<AutopistaFilter>,
    patentesSeleccionadas: List<PatenteFilter>,
    autopistasSeleccionadas: List<AutopistaFilter>,
    onPatenteToggle: (String) -> Unit,
    onAutopistaToggle: (String) -> Unit,
    onClearPatentes: () -> Unit,
    onClearAutopistas: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onDismiss) {
                    Text("Listo")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Sección Patentes ──────────────────────────
                Text(
                    text = "Patentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentBlue
                )

                if (patentes.isEmpty()) {
                    Text(
                        text = "No hay patentes disponibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${patentesSeleccionadas.size} de ${patentes.size} seleccionadas",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        if (patentesSeleccionadas.isNotEmpty()) {
                            TextButton(onClick = onClearPatentes) {
                                Text(
                                    "Limpiar",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    patentes.forEach { patente ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPatenteToggle(patente.patente) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = patente.isSelected,
                                onCheckedChange = { onPatenteToggle(patente.patente) },
                                colors = CheckboxDefaults.colors(checkedColor = NavyBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = patente.patente,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            if (patente.isSelected) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Seleccionada",
                                    tint = NavyBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                // ── Sección Autopistas ────────────────────────
                Text(
                    text = "Autopistas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentBlue
                )

                if (autopistas.isEmpty()) {
                    Text(
                        text = "No hay autopistas disponibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${autopistasSeleccionadas.size} de ${autopistas.size} seleccionadas",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        if (autopistasSeleccionadas.isNotEmpty()) {
                            TextButton(onClick = onClearAutopistas) {
                                Text(
                                    "Limpiar",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    autopistas.forEach { autopista ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAutopistaToggle(autopista.autopista) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically)
                        {
                            Checkbox(
                                checked = autopista.isSelected,
                                onCheckedChange = { onAutopistaToggle(autopista.autopista) },
                                colors = CheckboxDefaults.colors(checkedColor = NavyBlue))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = autopista.autopista,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f))
                            if (autopista.isSelected)
                            {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Seleccionada",
                                    tint = NavyBlue,
                                    modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {})
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