package com.example.wara.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wara.core.result.Resource
import com.example.wara.data.local.datastore.SessionDataStore
import com.example.wara.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effect = Channel<LoginUiEffect>(Channel.BUFFERED)
    val effect: Flow<LoginUiEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            sessionDataStore.baseUrlFlow.collect { url ->
                _uiState.update { it.copy(serverUrl = url) }
            }
        }
    }

    fun onIntent(intent: LoginUiIntent) {
        when (intent) {
            is LoginUiIntent.OnUsuarioChanged -> {
                _uiState.update { it.copy(usuario = intent.value, usuarioError = null, generalError = null) }
            }
            is LoginUiIntent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = intent.value, passwordError = null, generalError = null) }
            }
            is LoginUiIntent.OnTogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is LoginUiIntent.OnSubmitLogin -> submitLogin()
            is LoginUiIntent.OnClearError -> {
                _uiState.update { it.copy(generalError = null, usuarioError = null, passwordError = null) }
            }
            is LoginUiIntent.OnOpenServerConfig -> {
                _uiState.update { it.copy(isServerConfigVisible = true) }
            }
            is LoginUiIntent.OnCloseServerConfig -> {
                _uiState.update { it.copy(isServerConfigVisible = false) }
            }
            is LoginUiIntent.OnSaveServerUrl -> {
                viewModelScope.launch {
                    sessionDataStore.saveBaseUrl(intent.url)
                    _uiState.update { it.copy(isServerConfigVisible = false, generalError = null) }
                    _effect.send(LoginUiEffect.ShowSnackbar("Servidor configurado a: ${intent.url}"))
                }
            }
        }
    }

    private fun submitLogin() {
        val state = _uiState.value
        var hasError = false

        if (state.usuario.isBlank()) {
            _uiState.update { it.copy(usuarioError = "El usuario es obligatorio.") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "La contraseña es obligatoria.") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            when (val result = loginUseCase(state.usuario, state.password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(LoginUiEffect.NavigateToDashboard)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, generalError = result.message) }
                    _effect.send(LoginUiEffect.ShowSnackbar(result.message))
                }
                Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            loginUseCase: LoginUseCase,
            sessionDataStore: SessionDataStore
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(loginUseCase, sessionDataStore) as T
                }
            }
    }
}
