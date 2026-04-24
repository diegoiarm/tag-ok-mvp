package com.tagok.app.ui.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tagok.app.ui.theme.Blue40
import com.tagok.app.ui.theme.InputBackground
import com.tagok.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DarkButton = Color(0xFF1F2937)

private val tipoVehiculoOpciones = listOf(
    "AUTO" to "Auto",
    "MOTO" to "Motocicleta",
    "CAMIONETA" to "Camioneta",
    "BUS" to "Bus",
    "CAMION" to "Camión",
    "CAMION_REMOLQUE" to "Camión con remolque",
)

private val categoriaOpciones = listOf(
    "1" to "Categoría 1 — Motocicletas",
    "2" to "Categoría 2 — Autos y camionetas",
    "3" to "Categoría 3 — Buses",
    "4" to "Categoría 4 — Camiones 2 ejes",
    "5" to "Categoría 5 — Camiones 3 ejes",
    "6" to "Categoría 6 — Camiones 4 o más ejes",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onSuccess: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel(),
) {
    val form by viewModel.form.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var step by remember { mutableIntStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is RegisterUiState.Success -> onSuccess()
            is RegisterUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as RegisterUiState.Error).message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(52.dp))
            Text(
                text = "Registro",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(28.dp))

            when (step) {
                1 -> StepDatosPersonales(form, viewModel, onNext = { step = 2 })
                2 -> StepCredenciales(form, viewModel, onNext = { step = 3 })
                3 -> StepVehiculo(
                    form = form,
                    viewModel = viewModel,
                    isLoading = uiState is RegisterUiState.Loading,
                    onSave = viewModel::register,
                )
            }
            Spacer(Modifier.height(32.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = DarkButton,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepDatosPersonales(
    form: RegisterFormState,
    viewModel: RegisterViewModel,
    onNext: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        viewModel.updateFechaNacimiento(fmt.format(Date(millis)))
                    }
                    showDatePicker = false
                }) { Text("Confirmar", color = Blue40) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
        ) { DatePicker(state = datePickerState) }
    }

    Text(
        text = "Datos personales",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(20.dp))

    RegField("Nombre", form.nombre, viewModel::updateNombre)
    Spacer(Modifier.height(12.dp))
    RegField("Apellidos", form.apellidos, viewModel::updateApellidos)
    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = form.fechaNacimiento,
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha de nacimiento", style = MaterialTheme.typography.bodySmall) },
        placeholder = { Text("DD/MM/AAAA", style = MaterialTheme.typography.bodySmall) },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = Blue40)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = regFieldColors(),
        singleLine = true,
    )
    Spacer(Modifier.height(12.dp))
    RegField("Ciudad", form.ciudad, viewModel::updateCiudad)
    Spacer(Modifier.height(12.dp))
    RegField("Comuna", form.comuna, viewModel::updateComuna)
    Spacer(Modifier.height(28.dp))
    RegButton("Siguiente", onClick = onNext)
}

@Composable
private fun StepCredenciales(
    form: RegisterFormState,
    viewModel: RegisterViewModel,
    onNext: () -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatVisible by remember { mutableStateOf(false) }

    Text(
        text = "Datos personales",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(20.dp))

    RegField("Correo electrónico", form.email, viewModel::updateEmail, KeyboardType.Email)
    Spacer(Modifier.height(12.dp))
    RegField("Número celular", form.celular, viewModel::updateCelular, KeyboardType.Phone)
    Spacer(Modifier.height(12.dp))
    RegPasswordField(
        label = "Contraseña",
        value = form.password,
        visible = passwordVisible,
        onValueChange = viewModel::updatePassword,
        onToggle = { passwordVisible = !passwordVisible },
    )
    Spacer(Modifier.height(12.dp))
    RegPasswordField(
        label = "Repetir contraseña",
        value = form.repeatPassword,
        visible = repeatVisible,
        onValueChange = viewModel::updateRepeatPassword,
        onToggle = { repeatVisible = !repeatVisible },
    )
    Spacer(Modifier.height(28.dp))
    RegButton("Siguiente", onClick = onNext)
}

@Composable
private fun StepVehiculo(
    form: RegisterFormState,
    viewModel: RegisterViewModel,
    isLoading: Boolean,
    onSave: () -> Unit,
) {
    Text(
        text = "Vehículo",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(20.dp))

    RegDropdown(
        label = "Tipo de vehículo",
        opciones = tipoVehiculoOpciones,
        selectedValue = form.tipoVehiculo,
        onValueChange = viewModel::updateTipoVehiculo,
    )
    Spacer(Modifier.height(12.dp))
    RegDropdown(
        label = "Categoría de peaje",
        opciones = categoriaOpciones,
        selectedValue = form.categoria,
        onValueChange = viewModel::updateCategoria,
    )
    Spacer(Modifier.height(12.dp))
    RegField(
        label = "Patente",
        value = form.patente,
        onValueChange = viewModel::updatePatente,
        supportingText = "Ej: ABCD-12 (nuevo) o AB-1234 (antiguo)",
    )
    Spacer(Modifier.height(12.dp))
    RegField(
        label = "Número de TAG",
        value = form.numeroTag,
        onValueChange = viewModel::updateNumeroTag,
        keyboardType = KeyboardType.Number,
        supportingText = "Número impreso en el dispositivo TAG (10–12 dígitos)",
    )
    Spacer(Modifier.height(28.dp))

    OutlinedButton(
        onClick = viewModel::resetVehiculo,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DarkButton),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkButton),
    ) {
        Text("Registrar otro vehículo", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }

    Spacer(Modifier.height(12.dp))

    Button(
        onClick = onSave,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkButton),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        } else {
            Text("Guardar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun RegField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = regFieldColors(),
        supportingText = if (supportingText != null) {
            { Text(supportingText, style = MaterialTheme.typography.labelSmall, color = TextSecondary) }
        } else null,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegDropdown(
    label: String,
    opciones: List<Pair<String, String>>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = opciones.find { it.first == selectedValue }?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = regFieldColors(),
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            opciones.forEach { (value, displayLabel) ->
                DropdownMenuItem(
                    text = { Text(displayLabel, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onValueChange(value)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun RegPasswordField(
    label: String,
    value: String,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onToggle: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = TextSecondary,
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = regFieldColors(),
    )
}

@Composable
private fun RegButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkButton),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
private fun regFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = InputBackground,
    focusedContainerColor = InputBackground,
    unfocusedBorderColor = Color.Transparent,
    focusedBorderColor = Blue40,
    focusedLabelColor = Blue40,
)
