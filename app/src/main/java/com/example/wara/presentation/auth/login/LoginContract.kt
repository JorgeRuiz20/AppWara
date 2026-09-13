package com.example.wara.presentation.auth.login

data class LoginUiState(
    val usuario: String = "",
    val usuarioError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val serverUrl: String = "",
    val isServerConfigVisible: Boolean = false
)

sealed interface LoginUiIntent {
    data class OnUsuarioChanged(val value: String) : LoginUiIntent
    data class OnPasswordChanged(val value: String) : LoginUiIntent
    object OnTogglePasswordVisibility : LoginUiIntent
    object OnSubmitLogin : LoginUiIntent
    object OnClearError : LoginUiIntent
    object OnOpenServerConfig : LoginUiIntent
    object OnCloseServerConfig : LoginUiIntent
    data class OnSaveServerUrl(val url: String) : LoginUiIntent
}

sealed interface LoginUiEffect {
    object NavigateToDashboard : LoginUiEffect
    data class ShowSnackbar(val message: String) : LoginUiEffect
}
