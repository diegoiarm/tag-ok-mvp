package com.tagok.app.ui.historial.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tagok.app.ui.theme.LightBlueBg
import com.tagok.app.ui.theme.NavyBlue

@Composable
fun EnhancedDiaCalendario(
    day: Int,
    tieneDatos: Boolean,
    cantidad: Int,
    intensidad: Float,
    isToday: Boolean,
    onClick: () -> Unit)
{
    val backgroundColor = when
    {
        !tieneDatos && isToday -> NavyBlue.copy(alpha = 0.05f)
        !tieneDatos -> Color.White
        intensidad < 0.2f -> NavyBlue.copy(alpha = 0.1f)
        intensidad < 0.4f -> NavyBlue.copy(alpha = 0.25f)
        intensidad < 0.6f -> NavyBlue.copy(alpha = 0.45f)
        intensidad < 0.8f -> NavyBlue.copy(alpha = 0.65f)
        else -> NavyBlue
    }

    val borderColor = when
    {
        isToday -> NavyBlue
        !tieneDatos -> LightBlueBg
        else -> NavyBlue.copy(alpha = 0.3f + (intensidad * 0.7f))
    }

    val textColor = when
    {
        !tieneDatos && !isToday -> Color.Gray
        intensidad > 0.6f -> Color.White
        else -> NavyBlue
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isToday) 2.dp else if (tieneDatos) 1.5.dp else 1.dp,
                color = borderColor,
                shape = CircleShape)
            .clickable(enabled = tieneDatos, onClick = onClick),
        contentAlignment = Alignment.Center)
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text(
                text = "$day",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (tieneDatos || isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                fontSize = 14.sp)
            if (tieneDatos && cantidad > 0)
            {
                Text(
                    text = "$cantidad",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (intensidad > 0.6f) Color.White.copy(alpha = 0.8f) else NavyBlue.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}