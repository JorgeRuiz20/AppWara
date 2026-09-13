package com.example.wara.presentation.trabajador.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wara.core.result.Resource
import com.example.wara.domain.usecase.trabajador.ActualizarTrabajadorUseCase
import com.example.wara.domain.usecase.trabajador.GetTrabajadorByIdUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditTrabajadorViewModel(
    private val getTrabajadorByIdUseCase: GetTrabajadorByIdUseCase,
    private val actualizarTrabajadorUseCase: ActualizarTrabajadorUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTrabajadorUiState())
    val uiState: StateFlow<EditTrabajadorUiState> = _uiState.asStateFlow()

    private val _effect = Channel<EditTrabajadorUiEffect>(Channel.BUFFERED)
    val effect: Flow<EditTrabajadorUiEffect> = _effect.receiveAsFlow()

    fun onIntent(intent: EditTrabajadorUiIntent) {
        when (intent) {
            is EditTrabajadorUiIntent.LoadTrabajador -> loadTrabajador(intent.id)
            is EditTrabajadorUiIntent.OnNombreChanged -> {
                _uiState.update { it.copy(nombre = intent.value, nombreError = null, generalError = null) }
            }
            is EditTrabajadorUiIntent.OnApellidoChanged -> {
                _uiState.update { it.copy(apellido = intent.value, apellidoError = null, generalError = null) }
            }
            is EditTrabajadorUiIntent.OnEdadChanged -> {
                val digitsOnly = intent.value.filter { it.isDigit() }.take(3)
                _uiState.update { it.copy(edad = digitsOnly, edadError = null, generalError = null) }
            }
            is EditTrabajadorUiIntent.OnSubmit -> submitChanges()
            is EditTrabajadorUiIntent.OnClearError -> {
                _uiState.update { it.copy(generalError = null, nombreError = null, apellidoError = null, edadError = null) }
            }
        }
    }

    private fun loadTrabajador(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingData = true, generalError = null) }
            when (val res = getTrabajadorByIdUseCase(id)) {
                is Resource.Success -> {
                    val t = res.data
                    _uiState.update {
                        it.copy(
                            id = t.id,
                            nombre = t.nombre,
                            apellido = t.apellido,
                            dni = t.dni,
                            edad = t.edad.toString(),
                            isLoadingData = false
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingData = false, generalError = res.message) }
                    _effect.send(EditTrabajadorUiEffect.ShowSnackbar(res.message))
                }
                Resource.Loading -> {}
            }
        }
    }

    private fun submitChanges() {
        val state = _uiState.value
        var hasError = false

        val nombre = state.nombre.trim()
        val apellido = state.apellido.trim()
        val edad = state.edad.toIntOrNull()

        if (nombre.length < 2 || nombre.length > 100) {
            _uiState.update { it.copy(nombreError = "El nombre debe tener entre 2 y 100 caracteres.") }
            hasError = true
        }
        if (apellido.length < 2 || apellido.length > 100) {
            _uiState.update { it.copy(apellidoError = "El apellido debe tener entre 2 y 100 caracteres.") }
            hasError = true
        }
        if (edad == null || edad < 18 || edad > 80) {
            _uiState.update { it.copy(edadError = "La edad debe estar entre 18 y 80 años.") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, generalError = null) }
            when (val res = actualizarTrabajadorUseCase(state.id, nombre, apellido, edad!!)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _effect.send(EditTrabajadorUiEffect.NavigateBackWithSuccess("Trabajador actualizado exitosamente."))
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, generalError = res.message) }
                    _effect.send(EditTrabajadorUiEffect.ShowSnackbar(res.message))
                }
                Resource.Loading -> {
                    _uiState.update { it.copy(isSaving = true) }
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            getTrabajadorByIdUseCase: GetTrabajadorByIdUseCase,
            actualizarTrabajadorUseCase: ActualizarTrabajadorUseCase
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditTrabajadorViewModel(getTrabajadorByIdUseCase, actualizarTrabajadorUseCase) as T
            }
        }
    }
}
