package com.example.wara.presentation.auth.register

data class RegisterUiState(
    val usuario: String = "",
    val usuarioError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val generalError: String? = null
)

sealed interface RegisterUiIntent {
    data class OnUsuarioChanged(val value: String) : RegisterUiIntent
    data class OnPasswordChanged(val value: String) : RegisterUiIntent
    data class OnConfirmPasswordChanged(val value: String) : RegisterUiIntent
    object OnTogglePasswordVisibility : RegisterUiIntent
    object OnSubmitRegister : RegisterUiIntent
    object OnClearError : RegisterUiIntent
}

sealed interface RegisterUiEffect {
    data class NavigateToLoginWithSuccess(val message: String) : RegisterUiEffect
    data class ShowSnackbar(val message: String) : RegisterUiEffect
}
