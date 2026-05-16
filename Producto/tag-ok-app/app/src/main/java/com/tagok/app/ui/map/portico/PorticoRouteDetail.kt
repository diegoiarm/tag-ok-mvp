import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tagok.app.domain.model.routes.Portico
import com.tagok.app.domain.model.routes.Toll
import com.tagok.app.domain.model.routes.Tramo
import com.tagok.app.ui.map.portico.BasePorticoBottomSheet
import com.tagok.app.ui.map.portico.DetalleRow
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary

@Composable
fun PorticoRouteDetail(
    toll: Toll,
    onDismiss: () -> Unit)
{
    val title = when (toll)
    {
        is Portico -> "Pórtico: ${toll.nombre} (${toll.codigo})"
        is Tramo   -> "Tramo: ${toll.nombreEntrada} (${toll.codigoEntrada}) → ${toll.nombreSalida} (${toll.codigoSalida})"
    }

    BasePorticoBottomSheet(title = title, onDismiss = onDismiss)
    {
        when (toll)
        {
            is Portico -> {
                PorticoDetailContent(toll)
            }
            is Tramo -> {
                TramoDetailContent(toll)
            }
        }
    }
}

@Composable
fun PorticoDetailContent(portico: Portico)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp))
    {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = InputBackground))
        {
            Column(modifier = Modifier.padding(16.dp)) {
                DetalleRow("Autopista", portico.autopista)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                DetalleRow("Tarifa", portico.tarifa)
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Blue40.copy(alpha = 0.08f)))
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary)

                Text(
                    text = "$${portico.valor}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Blue40)
            }
        }

        // Fecha (si el campo está disponible, descomentá la siguiente línea)
        // Spacer(Modifier.height(4.dp))
        // DetalleRow("Fecha", portico.fechaHora)
    }
}

@Composable
fun TramoDetailContent(tramo: Tramo)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp))
    {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = InputBackground))
        {
            Column(modifier = Modifier.padding(16.dp)) {
                DetalleRow("Autopista", tramo.autopista)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                DetalleRow("Tarifa", tramo.tarifa)
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Blue40.copy(alpha = 0.08f)))
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary)

                Text(
                    text = "$${tramo.valor}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Blue40)
            }
        }
    }
}