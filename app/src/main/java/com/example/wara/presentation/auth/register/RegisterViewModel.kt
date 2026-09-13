package com.example.wara.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wara.core.result.Resource
import com.example.wara.core.validation.PasswordValidator
import com.example.wara.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _effect = Channel<RegisterUiEffect>(Channel.BUFFERED)
    val effect: Flow<RegisterUiEffect> = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterUiIntent) {
        when (intent) {
            is RegisterUiIntent.OnUsuarioChanged -> {
                _uiState.update { it.copy(usuario = intent.value, usuarioError = null, generalError = null) }
            }
            is RegisterUiIntent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = intent.value, passwordError = null, generalError = null) }
            }
            is RegisterUiIntent.OnConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = intent.value, confirmPasswordError = null, generalError = null) }
            }
            is RegisterUiIntent.OnTogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is RegisterUiIntent.OnSubmitRegister -> submitRegister()
            is RegisterUiIntent.OnClearError -> {
                _uiState.update { it.copy(generalError = null, usuarioError = null, passwordError = null) }
            }
        }
    }

    private fun submitRegister() {
        val state = _uiState.value
        var hasError = false

        if (state.usuario.trim().length < 3 || state.usuario.trim().length > 50) {
            _uiState.update { it.copy(usuarioError = "El nombre de usuario debe tener entre 3 y 50 caracteres.") }
            hasError = true
        }
        if (!PasswordValidator.isValid(state.password)) {
            _uiState.update { it.copy(passwordError = PasswordValidator.ERROR_MESSAGE) }
            hasError = true
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden.") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            when (val result = registerUseCase(state.usuario, state.password, state.confirmPassword)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(RegisterUiEffect.NavigateToLoginWithSuccess(result.data))
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, generalError = result.message) }
                    _effect.send(RegisterUiEffect.ShowSnackbar(result.message))
                }
                Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    companion object {
        fun provideFactory(registerUseCase: RegisterUseCase): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(registerUseCase) as T
                }
            }
    }
}
