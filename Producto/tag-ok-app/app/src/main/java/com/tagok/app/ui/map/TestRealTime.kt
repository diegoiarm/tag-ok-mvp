package com.tagok.app.ui.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TestRealTime()
{
    Card(
        modifier = Modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp))
    {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
        {
            Text(text = "Prueba")
            Button(onClick = { })
            {
                Text(text = "Calcular cruce")
            }
        }
    }
}