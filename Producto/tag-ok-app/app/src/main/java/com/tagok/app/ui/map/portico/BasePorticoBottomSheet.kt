package com.tagok.app.ui.map.portico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.ui.theme.NavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePorticoBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = NavyBlue,                     // fondo azul marino
        contentColor = Color.White                     // color de contenido por defecto (afecta textos si no se especifica)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,                // título blanco
                    modifier = Modifier.weight(1f)
                )
                // Si querés agregar un botón de cierre, lo podés incluir aquí
                // IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, ...) }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.White.copy(alpha = 0.2f) // divisor blanco semitransparente
            )

            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                content()
            }
        }
    }
}