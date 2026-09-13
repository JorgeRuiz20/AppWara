package com.example.wara.presentation.dashboard

import com.example.wara.domain.model.Trabajador

data class DashboardUiState(
    val usuarioActual: String = "Usuario",
    val trabajadores: List<Trabajador> = emptyList(),
    val filtroDni: String = "",
    val soloActivos: Boolean = true,
    val paginaActual: Int = 1,
    val totalPaginas: Int = 1,
    val totalRegistros: Int = 0,
    val tamanoPagina: Int = 10,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val selectedTrabajador: Trabajador? = null,
    val isPerfilSheetVisible: Boolean = false,
    val isAccionesSheetVisible: Boolean = false,
    val isDeleteDialogVisible: Boolean = false
)

sealed interface DashboardUiIntent {
    data class LoadData(val forceRefresh: Boolean = false) : DashboardUiIntent
    data class OnSearchDniChanged(val dni: String) : DashboardUiIntent
    object OnClearSearchDni : DashboardUiIntent
    data class OnFilterTabChanged(val soloActivos: Boolean) : DashboardUiIntent
    object OnPreviousPage : DashboardUiIntent
    object OnNextPage : DashboardUiIntent
    object OnOpenPerfilMenu : DashboardUiIntent
    object OnClosePerfilMenu : DashboardUiIntent
    data class OnOpenAccionesMenu(val trabajador: Trabajador) : DashboardUiIntent
    object OnCloseAccionesMenu : DashboardUiIntent
    object OnEditTrabajadorSelected : DashboardUiIntent
    object OnDeleteTrabajadorRequested : DashboardUiIntent
    object OnConfirmDeleteTrabajador : DashboardUiIntent
    object OnDismissDeleteDialog : DashboardUiIntent
    object OnLogout : DashboardUiIntent
    object OnFabAddClicked : DashboardUiIntent
}

sealed interface DashboardUiEffect {
    object NavigateToLogin : DashboardUiEffect
    object NavigateToAddTrabajador : DashboardUiEffect
    data class NavigateToEditTrabajador(val id: Int) : DashboardUiEffect
    data class ShowSnackbar(val message: String) : DashboardUiEffect
}
