package com.tagok.app.ui.historial.components.month

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.ui.historial.utils.getShortMonthName
import com.tagok.app.ui.theme.LightBlueBg
import com.tagok.app.ui.theme.NavyBlue

@Composable
fun AnimatedMonthCard(
    mes: Int,
    activo: Boolean,
    onClick: () -> Unit)
{
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = mes * 30,
                easing = FastOutSlowInEasing))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .scale(scale.value)
            .clickable(enabled = activo, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activo) NavyBlue else LightBlueBg),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (activo) 8.dp else 2.dp))
    {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center)
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = getShortMonthName(mes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium,
                    color = if (activo) Color.White else Color.Gray)
                if (activo)
                {
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        Icons.Filled.TrendingUp,
                        contentDescription = "Tiene datos",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}