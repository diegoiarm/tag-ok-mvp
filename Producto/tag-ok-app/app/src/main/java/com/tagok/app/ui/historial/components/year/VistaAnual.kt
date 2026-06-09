// components/year/VistaAnual.kt
package com.tagok.app.ui.historial.components.year

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.history.ResumenAnual
import com.tagok.app.ui.common.EmptyState

@Composable
fun VistaAnual(
    resumen: List<ResumenAnual>,
    onYearClick: (Int) -> Unit)
{
    if (resumen.isEmpty())
    {
        EmptyState()
        return
    }

    val maxCruces = resumen.maxOfOrNull { it.cantidadCruces } ?: 1

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp))
    {
        item {
            ResumenTotalCard(resumen = resumen)
        }

        items(resumen, key = { it.año }) { anual ->
            val animacionVisible = remember { Animatable(0f) }

            LaunchedEffect(anual.año) {
                animacionVisible.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = resumen.indexOf(anual) * 100))
            }

            AnimatedYearCard(
                anual = anual,
                maxCruces = maxCruces,
                onClick = { onYearClick(anual.año) },
                modifier = Modifier
                    .alpha(animacionVisible.value)
                    .scale(0.8f + (0.2f * animacionVisible.value)))
        }
    }
}