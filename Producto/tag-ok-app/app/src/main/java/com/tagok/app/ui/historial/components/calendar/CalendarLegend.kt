// components/calendar/CalendarLegend.kt
package com.tagok.app.ui.historial.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.tagok.app.ui.theme.LightBlueBg
import com.tagok.app.ui.theme.NavyBlue

@Composable
fun CalendarLegend()
{
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightBlueBg))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically)
        {
            Icon(
                Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Menos",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray)
            Spacer(modifier = Modifier.width(6.dp))
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            NavyBlue.copy(alpha = 0.1f + (index * 0.22f))))
                Spacer(modifier = Modifier.width(3.dp))
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Más",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray)
        }
    }
}