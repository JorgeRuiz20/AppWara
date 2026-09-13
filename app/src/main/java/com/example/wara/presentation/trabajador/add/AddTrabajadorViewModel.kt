package com.example.wara.presentation.trabajador.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wara.core.result.Resource
import com.example.wara.domain.usecase.trabajador.CrearTrabajadorUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddTrabajadorViewModel(
    private val crearTrabajadorUseCase: CrearTrabajadorUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTrabajadorUiState())
    val uiState: StateFlow<AddTrabajadorUiState> = _uiState.asStateFlow()

    private val _effect = Channel<AddTrabajadorUiEffect>(Channel.BUFFERED)
    val effect: Flow<AddTrabajadorUiEffect> = _effect.receiveAsFlow()

    fun onIntent(intent: AddTrabajadorUiIntent) {
        when (intent) {
            is AddTrabajadorUiIntent.OnNombreChanged -> {
                _uiState.update { it.copy(nombre = intent.value, nombreError = null, generalError = null) }
            }
            is AddTrabajadorUiIntent.OnApellidoChanged -> {
                _uiState.update { it.copy(apellido = intent.value, apellidoError = null, generalError = null) }
            }
            is AddTrabajadorUiIntent.OnDniChanged -> {
                val digitsOnly = intent.value.filter { it.isDigit() }.take(8)
                _uiState.update { it.copy(dni = digitsOnly, dniError = null, generalError = null) }
            }
            is AddTrabajadorUiIntent.OnEdadChanged -> {
                val digitsOnly = intent.value.filter { it.isDigit() }.take(3)
                _uiState.update { it.copy(edad = digitsOnly, edadError = null, generalError = null) }
            }
            is AddTrabajadorUiIntent.OnSubmit -> submitForm()
            is AddTrabajadorUiIntent.OnClearError -> {
                _uiState.update { it.copy(generalError = null, nombreError = null, apellidoError = null, dniError = null, edadError = null) }
            }
        }
    }

    private fun submitForm() {
        val state = _uiState.value
        var hasError = false

        val nombre = state.nombre.trim()
        val apellido = state.apellido.trim()
        val dni = state.dni.trim()
        val edad = state.edad.toIntOrNull()

        if (nombre.length < 2 || nombre.length > 100) {
            _uiState.update { it.copy(nombreError = "El nombre debe tener entre 2 y 100 caracteres.") }
            hasError = true
        }
        if (apellido.length < 2 || apellido.length > 100) {
            _uiState.update { it.copy(apellidoError = "El apellido debe tener entre 2 y 100 caracteres.") }
            hasError = true
        }
        if (!dni.matches(Regex("^\\d{8}$"))) {
            _uiState.update { it.copy(dniError = "El DNI debe tener exactamente 8 dígitos numéricos.") }
            hasError = true
        }
        if (edad == null || edad < 18 || edad > 80) {
            _uiState.update { it.copy(edadError = "La edad debe estar entre 18 y 80 años.") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            when (val res = crearTrabajadorUseCase(nombre, apellido, dni, edad!!)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(AddTrabajadorUiEffect.NavigateBackWithSuccess("Trabajador registrado exitosamente."))
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, generalError = res.message) }
                    _effect.send(AddTrabajadorUiEffect.ShowSnackbar(res.message))
                }
                Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    companion object {
        fun provideFactory(crearTrabajadorUseCase: CrearTrabajadorUseCase): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddTrabajadorViewModel(crearTrabajadorUseCase) as T
                }
            }
    }
}
