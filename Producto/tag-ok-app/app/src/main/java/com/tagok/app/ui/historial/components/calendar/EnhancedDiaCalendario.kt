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
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

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
        !tieneDatos && isToday -> Blue40.copy(alpha = 0.05f)
        !tieneDatos -> Color.White
        intensidad < 0.2f -> Blue40.copy(alpha = 0.1f)
        intensidad < 0.4f -> Blue40.copy(alpha = 0.25f)
        intensidad < 0.6f -> Blue40.copy(alpha = 0.45f)
        intensidad < 0.8f -> Blue40.copy(alpha = 0.65f)
        else -> Blue40
    }

    val borderColor = when
    {
        isToday -> Blue40
        !tieneDatos -> InputBackground
        else -> Blue40.copy(alpha = 0.3f + (intensidad * 0.7f))
    }

    val textColor = when
    {
        !tieneDatos && !isToday -> TextSecondary
        intensidad > 0.6f -> Color.White
        else -> Blue40
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
                    color = if (intensidad > 0.6f) Color.White.copy(alpha = 0.8f) else Blue40.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}