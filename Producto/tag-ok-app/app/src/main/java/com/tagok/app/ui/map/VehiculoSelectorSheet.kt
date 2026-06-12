package com.tagok.app.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.domain.model.vehiculo.Vehiculo
import com.tagok.app.ui.vehiculos.VehiculosUiState
import com.tagok.app.ui.vehiculos.VehiculosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculoSelectorSheet(
    selected: Vehiculo?,
    onSelect: (Vehiculo) -> Unit,
    onDismiss: () -> Unit,
    vehiculosViewModel: VehiculosViewModel = viewModel(factory = VehiculosViewModel.Factory))
{
    val vehiculos by vehiculosViewModel.vehiculos.collectAsState()
    val uiState by vehiculosViewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState)
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp))
        {
            Text(
                text = "Seleccioná un vehículo",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp))

            when (uiState)
            {
                is VehiculosUiState.Loading ->
                    {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center)
                        {
                            CircularProgressIndicator()
                        }
                }
                is VehiculosUiState.Error ->
                    {
                    Text(
                        text = (uiState as VehiculosUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 24.dp))
                }
                else -> {
                    if (vehiculos.isEmpty())
                    {
                        Text(
                            text = "No hay vehículos registrados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp))
                    }
                    else
                    {
                        LazyColumn()
                        {
                            items(
                                items = vehiculos,
                                key = { it.id }) { vehiculo ->
                                val isSelected = vehiculo.id == selected?.id
                                Surface(
                                    onClick = { onSelect(vehiculo) },
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp))
                                {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically)
                                    {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            tint = if (isSelected)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant)

                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            vehiculo.alias?.let {
                                                Text(
                                                    text = it,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                            }
                                            Text(
                                                text = vehiculo.patente,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        if (isSelected)
                                        {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Seleccionado",
                                                tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}