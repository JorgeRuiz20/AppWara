package com.example.wara.presentation.trabajador.add

data class AddTrabajadorUiState(
    val nombre: String = "",
    val nombreError: String? = null,
    val apellido: String = "",
    val apellidoError: String? = null,
    val dni: String = "",
    val dniError: String? = null,
    val edad: String = "",
    val edadError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null
)

sealed interface AddTrabajadorUiIntent {
    data class OnNombreChanged(val value: String) : AddTrabajadorUiIntent
    data class OnApellidoChanged(val value: String) : AddTrabajadorUiIntent
    data class OnDniChanged(val value: String) : AddTrabajadorUiIntent
    data class OnEdadChanged(val value: String) : AddTrabajadorUiIntent
    object OnSubmit : AddTrabajadorUiIntent
    object OnClearError : AddTrabajadorUiIntent
}

sealed interface AddTrabajadorUiEffect {
    data class NavigateBackWithSuccess(val message: String) : AddTrabajadorUiEffect
    data class ShowSnackbar(val message: String) : AddTrabajadorUiEffect
}
