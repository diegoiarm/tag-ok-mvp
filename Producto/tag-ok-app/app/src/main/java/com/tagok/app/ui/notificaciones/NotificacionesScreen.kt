package com.tagok.app.ui.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.domain.model.notificacion.Notificacion

private val NavyBlue   = Color(0xFF172955)
private val AccentBlue = Color(0xFF1C42B1)
private val PageBg     = Color(0xFFF4F6FB)
private val UnreadBg   = Color(0xFFEEF2FF)
private val TextDark   = Color(0xFF111827)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    onBack: () -> Unit,
    viewModel: NotificacionesViewModel = viewModel(factory = NotificacionesViewModel.Factory))
{
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", fontWeight = FontWeight.Bold, color = NavyBlue) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = NavyBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White))
        })
    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding))
        {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NavyBlue,
                        strokeWidth = 2.dp)
                }
                state.items.isEmpty() -> {
                    EmptyState(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        items(state.items, key = { it.id }) { n ->
                            NotificacionRow(n = n, onClick = { if (!n.leida) viewModel.marcarLeida(n.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificacionRow(n: Notificacion, onClick: () -> Unit)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (n.leida) Color.White else UnreadBg)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp))
    {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(UnreadBg),
            contentAlignment = Alignment.Center)
        {
            Icon(
                Icons.Filled.NotificationsActive,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.fillMaxWidth())
        {
            Text(
                text = n.titulo,
                fontSize = 14.sp,
                fontWeight = if (n.leida) FontWeight.SemiBold else FontWeight.Bold,
                color = TextDark)
            Spacer(Modifier.height(2.dp))
            Text(text = n.cuerpo, fontSize = 13.sp, color = Color.Gray)
            n.createdAt?.let { fecha ->
                Spacer(Modifier.height(4.dp))
                Text(text = formatearFecha(fecha), fontSize = 11.sp, color = Color(0xFF9CA3AF))
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Icon(
            Icons.Outlined.NotificationsNone,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(12.dp))
        Text("Sin notificaciones", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
        Spacer(Modifier.height(4.dp))
        Text(
            "Cuando te acerques al límite de tu presupuesto, te avisaremos aquí.",
            fontSize = 13.sp,
            color = Color.Gray)
    }
}

/** Recorta el timestamp ISO de Supabase a 'YYYY-MM-DD HH:mm'. */
private fun formatearFecha(iso: String): String
{
    val limpio = iso.replace('T', ' ')
    return if (limpio.length >= 16) limpio.substring(0, 16) else limpio
}
