package com.tagok.app.ui.perfil

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextPrimary
import com.tagok.app.ui.theme.TextSecondary

private val SurfaceGray  = Color(0xFFF3F4F6)
private val DividerGray  = Color(0xFFE5E7EB)
private val TextDark     = Color(0xFF111827)

@Composable
fun PerfilScreen(
    onVehiculos: () -> Unit = {},
    onMisRutas: () -> Unit = {},
    viewModel: PerfilViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMsg) {
        state.errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SurfaceGray,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(36.dp))

            AvatarSection(nombre = state.nombre, apellidos = state.apellidos)

            Spacer(Modifier.height(24.dp))

            AnimatedContent(
                targetState = state.isEditing,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "perfil_mode",
            ) { editing ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (editing) {
                        EditSection(
                            state = state,
                            onNombre    = viewModel::updateNombre,
                            onApellidos = viewModel::updateApellidos,
                            onCelular   = viewModel::updateCelular,
                            onCiudad    = viewModel::updateCiudad,
                        )
                    } else {
                        InfoSection(state = state)
                        Spacer(Modifier.height(12.dp))
                        NavSection(onVehiculos = onVehiculos, onMisRutas = onMisRutas)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            BottomActions(
                isEditing = state.isEditing,
                isSaving  = state.isSaving,
                onEditar  = viewModel::startEditing,
                onGuardar = viewModel::guardar,
                onCancelar = viewModel::cancelEditing,
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Avatar ───────────────────────────────────────────────────────────────────
@Composable
private fun AvatarSection(nombre: String, apellidos: String) {
    val initials = buildString {
        nombre.firstOrNull()?.let { append(it.uppercaseChar()) }
        apellidos.firstOrNull()?.let { append(it.uppercaseChar()) }
        if (isEmpty()) append("U")
    }

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(InputBackground),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            color = Blue40,
        )
    }

    Spacer(Modifier.height(14.dp))

    Text(
        text = "$nombre $apellidos".trim().ifBlank { "Usuario" },
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = TextDark,
    )
}

// ─── Info (read-only) ─────────────────────────────────────────────────────────
@Composable
private fun InfoSection(state: PerfilState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            InfoRow(Icons.Filled.Phone, state.celular.ifBlank { "Sin teléfono" }, state.celular.isBlank())
            RowDivider()
            InfoRow(Icons.Filled.Email, state.email.ifBlank { "Sin correo" }, state.email.isBlank())
            RowDivider()
            InfoRow(Icons.Filled.Home, state.ciudad.ifBlank { "Sin ciudad" }, state.ciudad.isBlank())
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, value: String, faded: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = if (faded) TextSecondary else Blue40, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = if (faded) TextSecondary else TextPrimary,
        )
    }
}

// ─── Navigation rows ──────────────────────────────────────────────────────────
@Composable
private fun NavSection(onVehiculos: () -> Unit, onMisRutas: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            NavRow(Icons.Filled.DirectionsCar, "Vehículos", onVehiculos)
            RowDivider()
            NavRow(Icons.Filled.Star, "Mis rutas", onMisRutas)
        }
    }
}

@Composable
private fun NavRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, null, tint = Blue40, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(14.dp))
            Text(label, fontSize = 14.sp, color = TextPrimary, modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ─── Edit mode ────────────────────────────────────────────────────────────────
@Composable
private fun EditSection(
    state: PerfilState,
    onNombre: (String) -> Unit,
    onApellidos: (String) -> Unit,
    onCelular: (String) -> Unit,
    onCiudad: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            EditField("Nombre",    state.nombre,    onNombre)
            Spacer(Modifier.height(10.dp))
            EditField("Apellidos", state.apellidos, onApellidos)
            Spacer(Modifier.height(10.dp))
            EditField("Teléfono", state.celular, onCelular, KeyboardType.Phone)
            Spacer(Modifier.height(10.dp))
            EditField("Ciudad",    state.ciudad,    onCiudad)
            Spacer(Modifier.height(12.dp))
            // Email no editable
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceGray)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Email, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(state.email, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                Text("· no editable", fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun EditField(
    label: String,
    value: String,
    onValue: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue40,
            unfocusedBorderColor = DividerGray,
            focusedLabelColor = Blue40,
        ),
    )
}

// ─── Bottom actions ───────────────────────────────────────────────────────────
@Composable
private fun BottomActions(
    isEditing: Boolean,
    isSaving: Boolean,
    onEditar: () -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
) {
    if (isEditing) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TextButton(
                onClick = onCancelar,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary),
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onGuardar,
                enabled = !isSaving,
                modifier = Modifier
                    .weight(2f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue40),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Guardar", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    } else {
        Button(
            onClick = onEditar,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue40),
        ) {
            Text("Editar", fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Helper ───────────────────────────────────────────────────────────────────
@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 52.dp, end = 18.dp),
        color = DividerGray,
    )
}
