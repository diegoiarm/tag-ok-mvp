package com.tagok.app.ui.components.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tagok.app.data.GeocodeSuggestion
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun DireccionField(
    value: String,
    onValueChange: (String) -> Unit,
    sugerencias: List<GeocodeSuggestion>,
    onSugerenciaClick: (GeocodeSuggestion) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    cargando: Boolean)
{
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            placeholder = { Text("Dirección, negocio o lugar", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Blue40,
                    modifier = Modifier.size(18.dp),
                )
            },
            trailingIcon = {
                if (cargando)
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Blue40)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue40,
                unfocusedBorderColor = InputBackground,
                focusedLabelColor = Blue40,
            ),
        )

        if (sugerencias.isNotEmpty())
        {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp))
            {
                Column {
                    sugerencias.take(5).forEachIndexed { index, sugerencia ->
                        TextButton(
                            onClick = { onSugerenciaClick(sugerencia) },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        )
                        {
                            Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp))

                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = sugerencia.placeName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f))
                        }

                        if (index < sugerencias.size - 1)
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                color = InputBackground)
                    }
                }
            }
        }
    }
}