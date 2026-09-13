package com.example.wara.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wara.core.result.Resource
import com.example.wara.domain.model.Trabajador
import com.example.wara.domain.usecase.auth.GetSavedSessionUseCase
import com.example.wara.domain.usecase.auth.GetUsuarioActualUseCase
import com.example.wara.domain.usecase.auth.LogoutUseCase
import com.example.wara.domain.usecase.trabajador.EliminarTrabajadorUseCase
import com.example.wara.domain.usecase.trabajador.GetTrabajadoresUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getSavedSessionUseCase: GetSavedSessionUseCase,
    private val getUsuarioActualUseCase: GetUsuarioActualUseCase,
    private val getTrabajadoresUseCase: GetTrabajadoresUseCase,
    private val eliminarTrabajadorUseCase: EliminarTrabajadorUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _effect = Channel<DashboardUiEffect>(Channel.BUFFERED)
    val effect: Flow<DashboardUiEffect> = _effect.receiveAsFlow()

    private var searchDebounceJob: Job? = null

    init {
        observeSavedSession()
        loadUsuarioActual()
        loadTrabajadores()
    }

    private fun observeSavedSession() {
        viewModelScope.launch {
            getSavedSessionUseCase().collect { (token, username) ->
                if (!username.isNullOrBlank()) {
                    _uiState.update { it.copy(usuarioActual = username) }
                }
            }
        }
    }

    private fun loadUsuarioActual() {
        viewModelScope.launch {
            when (val res = getUsuarioActualUseCase()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(usuarioActual = res.data.nombreUsuario) }
                }
                else -> { /* Ignore if fails, use cached session name */ }
            }
        }
    }

    fun onIntent(intent: DashboardUiIntent) {
        when (intent) {
            is DashboardUiIntent.LoadData -> loadTrabajadores(forceRefresh = intent.forceRefresh)
            is DashboardUiIntent.OnSearchDniChanged -> {
                _uiState.update { it.copy(filtroDni = intent.dni, paginaActual = 1) }
                searchDebounceJob?.cancel()
                searchDebounceJob = viewModelScope.launch {
                    delay(400)
                    loadTrabajadores()
                }
            }
            is DashboardUiIntent.OnClearSearchDni -> {
                _uiState.update { it.copy(filtroDni = "", paginaActual = 1) }
                searchDebounceJob?.cancel()
                loadTrabajadores()
            }
            is DashboardUiIntent.OnFilterTabChanged -> {
                if (_uiState.value.soloActivos != intent.soloActivos) {
                    _uiState.update { it.copy(soloActivos = intent.soloActivos, paginaActual = 1) }
                    loadTrabajadores()
                }
            }
            is DashboardUiIntent.OnPreviousPage -> {
                val current = _uiState.value.paginaActual
                if (current > 1) {
                    _uiState.update { it.copy(paginaActual = current - 1) }
                    loadTrabajadores()
                }
            }
            is DashboardUiIntent.OnNextPage -> {
                val current = _uiState.value.paginaActual
                val total = _uiState.value.totalPaginas
                if (current < total) {
                    _uiState.update { it.copy(paginaActual = current + 1) }
                    loadTrabajadores()
                }
            }
            is DashboardUiIntent.OnOpenPerfilMenu -> {
                _uiState.update { it.copy(isPerfilSheetVisible = true) }
            }
            is DashboardUiIntent.OnClosePerfilMenu -> {
                _uiState.update { it.copy(isPerfilSheetVisible = false) }
            }
            is DashboardUiIntent.OnOpenAccionesMenu -> {
                _uiState.update {
                    it.copy(
                        selectedTrabajador = intent.trabajador,
                        isAccionesSheetVisible = true
                    )
                }
            }
            is DashboardUiIntent.OnCloseAccionesMenu -> {
                _uiState.update { it.copy(isAccionesSheetVisible = false) }
            }
            is DashboardUiIntent.OnEditTrabajadorSelected -> {
                val trabajador = _uiState.value.selectedTrabajador
                _uiState.update { it.copy(isAccionesSheetVisible = false) }
                if (trabajador != null) {
                    viewModelScope.launch {
                        _effect.send(DashboardUiEffect.NavigateToEditTrabajador(trabajador.id))
                    }
                }
            }
            is DashboardUiIntent.OnDeleteTrabajadorRequested -> {
                _uiState.update {
                    it.copy(
                        isAccionesSheetVisible = false,
                        isDeleteDialogVisible = true
                    )
                }
            }
            is DashboardUiIntent.OnDismissDeleteDialog -> {
                _uiState.update { it.copy(isDeleteDialogVisible = false) }
            }
            is DashboardUiIntent.OnConfirmDeleteTrabajador -> {
                confirmDeleteTrabajador()
            }
            is DashboardUiIntent.OnLogout -> {
                _uiState.update { it.copy(isPerfilSheetVisible = false) }
                viewModelScope.launch {
                    logoutUseCase()
                    _effect.send(DashboardUiEffect.NavigateToLogin)
                }
            }
            is DashboardUiIntent.OnFabAddClicked -> {
                viewModelScope.launch {
                    _effect.send(DashboardUiEffect.NavigateToAddTrabajador)
                }
            }
        }
    }

    private fun loadTrabajadores(forceRefresh: Boolean = false) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getTrabajadoresUseCase(
                dni = state.filtroDni,
                pagina = state.paginaActual,
                tamanoPagina = state.tamanoPagina,
                soloActivos = state.soloActivos,
                forceRefresh = forceRefresh
            )
            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            trabajadores = result.data.items,
                            paginaActual = result.data.pagina,
                            totalPaginas = if (result.data.totalPaginas < 1) 1 else result.data.totalPaginas,
                            totalRegistros = result.data.totalRegistros
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun confirmDeleteTrabajador() {
        val trabajador = _uiState.value.selectedTrabajador ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleteDialogVisible = false, isLoading = true) }
            when (val res = eliminarTrabajadorUseCase(trabajador.id)) {
                is Resource.Success -> {
                    _effect.send(DashboardUiEffect.ShowSnackbar("Trabajador eliminado exitosamente."))
                    loadTrabajadores(forceRefresh = true)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(DashboardUiEffect.ShowSnackbar(res.message))
                }
                Resource.Loading -> {}
            }
        }
    }

    companion object {
        fun provideFactory(
            getSavedSessionUseCase: GetSavedSessionUseCase,
            getUsuarioActualUseCase: GetUsuarioActualUseCase,
            getTrabajadoresUseCase: GetTrabajadoresUseCase,
            eliminarTrabajadorUseCase: EliminarTrabajadorUseCase,
            logoutUseCase: LogoutUseCase
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(
                    getSavedSessionUseCase,
                    getUsuarioActualUseCase,
                    getTrabajadoresUseCase,
                    eliminarTrabajadorUseCase,
                    logoutUseCase
                ) as T
            }
        }
    }
}
