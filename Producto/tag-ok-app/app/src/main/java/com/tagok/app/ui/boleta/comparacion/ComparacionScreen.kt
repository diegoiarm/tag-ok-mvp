package com.tagok.app.ui.boleta.comparacion

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Verificación de factura con IA: el usuario adjunta la factura de su
 * concesionaria (PDF o foto) y la app la compara contra la boleta TAG OK
 * del mismo período.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparacionScreen(
    patente: String,
    fechaDesde: LocalDate,
    fechaHasta: LocalDate,
    autopistas: List<String>,
    onBack: () -> Unit = {},
    viewModel: ComparacionViewModel = viewModel(
        factory = ComparacionViewModel.factory(patente, fechaDesde, fechaHasta, autopistas)))
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun cargarArchivo(uri: Uri)
    {
        scope.launch {
            viewModel.setCargandoArchivo(true)
            try
            {
                viewModel.onArchivoSeleccionado(FacturaPicker.cargar(context, uri))
            }
            catch (e: Exception)
            {
                viewModel.mostrarError("No se pudo leer el archivo seleccionado")
            }
        }
    }

    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    // OpenDocument (en vez de GetContent) entrega un Uri con permiso persistible:
    // más robusto si el sistema recrea el proceso mientras el selector está abierto.
    val lanzadorPdf = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri -> uri?.let { cargarArchivo(it) } }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri -> uri?.let { cargarArchivo(it) } }

    val lanzadorCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()) { exito ->
        if (exito) fotoUri?.let { cargarArchivo(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verificar factura") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                })
        })
    { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding))
        {
            val resultado = uiState.resultado

            if (resultado != null)
            {
                ResultadoComparacionContent(
                    resultado = resultado,
                    onNuevaComparacion = viewModel::nuevaComparacion)
            }
            else
            {
                SeleccionArchivoContent(
                    uiState = uiState,
                    onTomarFoto = {
                        val uri = FacturaPicker.crearUriFoto(context)
                        fotoUri = uri
                        lanzadorCamara.launch(uri)
                    },
                    onSubirPdf = { lanzadorPdf.launch(arrayOf("application/pdf")) },
                    onElegirImagen = { lanzadorGaleria.launch("image/*") },
                    onQuitarArchivo = viewModel::quitarArchivo,
                    onComparar = viewModel::comparar)
            }

            if (uiState.isComparando)
            {
                AnalizandoOverlay()
            }
        }
    }

    if (uiState.error != null)
    {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Text(
                    text = "No se pudo comparar",
                    fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = uiState.error ?: "",
                    modifier = Modifier.padding(vertical = 8.dp))
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearError() },
                    modifier = Modifier.padding(8.dp))
                {
                    Text("Entendido")
                }
            })
    }
}

@Composable
private fun SeleccionArchivoContent(
    uiState: ComparacionUiState,
    onTomarFoto: () -> Unit,
    onSubirPdf: () -> Unit,
    onElegirImagen: () -> Unit,
    onQuitarArchivo: () -> Unit,
    onComparar: () -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp))
    {
        ContextoBoletaCard(
            patente = uiState.patente,
            fechaDesde = uiState.fechaDesde,
            fechaHasta = uiState.fechaHasta,
            autopistas = uiState.autopistas)

        val archivo = uiState.archivo

        if (archivo == null)
        {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp))
            {
                Text(
                    text = "Adjunta la factura de tu concesionaria",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)

                Text(
                    text = "La IA leerá los cruces cobrados en tu factura y los " +
                        "comparará uno a uno con lo registrado por TAG OK.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                OpcionAdjuntoRow(
                    icon = Icons.Outlined.PhotoCamera,
                    titulo = "Tomar una foto",
                    subtitulo = "Escanea la factura con la cámara",
                    enabled = !uiState.cargandoArchivo,
                    onClick = onTomarFoto)

                OpcionAdjuntoRow(
                    icon = Icons.Outlined.PictureAsPdf,
                    titulo = "Subir PDF",
                    subtitulo = "El documento que descargaste de tu concesionaria",
                    enabled = !uiState.cargandoArchivo,
                    onClick = onSubirPdf)

                OpcionAdjuntoRow(
                    icon = Icons.Outlined.Image,
                    titulo = "Elegir de la galería",
                    subtitulo = "Una foto o captura guardada en tu teléfono",
                    enabled = !uiState.cargandoArchivo,
                    onClick = onElegirImagen)

                if (uiState.cargandoArchivo)
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Preparando archivo...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        else
        {
            ArchivoSeleccionadoCard(
                archivo = archivo,
                onQuitar = onQuitarArchivo)

            Button(
                onClick = onComparar,
                enabled = !uiState.isComparando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium)
            {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Comparar con IA",
                    style = MaterialTheme.typography.titleMedium)
            }
        }

        Text(
            text = "La lectura del documento se realiza con inteligencia artificial " +
                "y puede contener errores. Ante diferencias, verifica siempre con tu concesionaria.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(16.dp))
    }
}
