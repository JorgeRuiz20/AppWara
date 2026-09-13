package com.example.wara.presentation.trabajador.edit

data class EditTrabajadorUiState(
    val id: Int = 0,
    val nombre: String = "",
    val nombreError: String? = null,
    val apellido: String = "",
    val apellidoError: String? = null,
    val dni: String = "",
    val edad: String = "",
    val edadError: String? = null,
    val isLoadingData: Boolean = false,
    val isSaving: Boolean = false,
    val generalError: String? = null
)

sealed interface EditTrabajadorUiIntent {
    data class LoadTrabajador(val id: Int) : EditTrabajadorUiIntent
    data class OnNombreChanged(val value: String) : EditTrabajadorUiIntent
    data class OnApellidoChanged(val value: String) : EditTrabajadorUiIntent
    data class OnEdadChanged(val value: String) : EditTrabajadorUiIntent
    object OnSubmit : EditTrabajadorUiIntent
    object OnClearError : EditTrabajadorUiIntent
}

sealed interface EditTrabajadorUiEffect {
    data class NavigateBackWithSuccess(val message: String) : EditTrabajadorUiEffect
    data class ShowSnackbar(val message: String) : EditTrabajadorUiEffect
}
