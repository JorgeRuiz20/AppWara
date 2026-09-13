package com.example.wara.presentation.trabajador.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wara.presentation.components.ApiFootnote
import com.example.wara.presentation.components.WaraPrimaryButton
import com.example.wara.presentation.components.WaraSecondaryButton
import com.example.wara.presentation.components.WaraTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTrabajadorScreen(
    viewModel: AddTrabajadorViewModel,
    onNavigateBack: () -> Unit,
    onWorkerAdded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddTrabajadorUiEffect.NavigateBackWithSuccess -> onWorkerAdded(effect.message)
                is AddTrabajadorUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agregar trabajador",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Error banner
            if (!state.generalError.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = state.generalError!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Nombre
            WaraTextField(
                value = state.nombre,
                onValueChange = { viewModel.onIntent(AddTrabajadorUiIntent.OnNombreChanged(it)) },
                label = "Nombre",
                errorMessage = state.nombreError,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Apellido
            WaraTextField(
                value = state.apellido,
                onValueChange = { viewModel.onIntent(AddTrabajadorUiIntent.OnApellidoChanged(it)) },
                label = "Apellido",
                errorMessage = state.apellidoError,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DNI
            WaraTextField(
                value = state.dni,
                onValueChange = { viewModel.onIntent(AddTrabajadorUiIntent.OnDniChanged(it)) },
                label = "DNI",
                helperText = "8 dígitos numéricos",
                errorMessage = state.dniError,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Edad
            WaraTextField(
                value = state.edad,
                onValueChange = { viewModel.onIntent(AddTrabajadorUiIntent.OnEdadChanged(it)) },
                label = "Edad",
                helperText = "Entre 18 y 80 años",
                errorMessage = state.edadError,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.onIntent(AddTrabajadorUiIntent.OnSubmit)
                    }
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Guardar trabajador
            WaraPrimaryButton(
                text = "Guardar trabajador",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onIntent(AddTrabajadorUiIntent.OnSubmit)
                },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cancelar
            WaraSecondaryButton(
                text = "Cancelar",
                onClick = onNavigateBack
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
