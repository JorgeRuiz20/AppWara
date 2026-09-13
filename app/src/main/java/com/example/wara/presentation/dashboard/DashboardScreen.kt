package com.example.wara.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wara.domain.model.Trabajador
import com.example.wara.presentation.components.WorkerAvatar
import com.example.wara.presentation.dashboard.components.MenuAccionesBottomSheet
import com.example.wara.presentation.dashboard.components.MenuPerfilBottomSheet
import com.example.wara.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToAddTrabajador: () -> Unit,
    onNavigateToEditTrabajador: (Int) -> Unit,
    externalMessage: String? = null,
    onClearExternalMessage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(externalMessage) {
        externalMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearExternalMessage()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DashboardUiEffect.NavigateToLogin -> onNavigateToLogin()
                is DashboardUiEffect.NavigateToAddTrabajador -> onNavigateToAddTrabajador()
                is DashboardUiEffect.NavigateToEditTrabajador -> onNavigateToEditTrabajador(effect.id)
                is DashboardUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onIntent(DashboardUiIntent.OnFabAddClicked) },
                shape = RoundedCornerShape(16.dp),
                containerColor = WaraPrimaryContainer,
                contentColor = WaraOnPrimaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar trabajador",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        bottomBar = {
            // Pagination Bar con soporte para navigationBarsPadding (evita solapamiento con botones del sistema)
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { viewModel.onIntent(DashboardUiIntent.OnPreviousPage) },
                        enabled = state.paginaActual > 1
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Página anterior",
                            tint = if (state.paginaActual > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                        )
                    }

                    Text(
                        text = "Página ${state.paginaActual} de ${state.totalPaginas} · ${state.totalRegistros} registros",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { viewModel.onIntent(DashboardUiIntent.OnNextPage) },
                        enabled = state.paginaActual < state.totalPaginas
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Página siguiente",
                            tint = if (state.paginaActual < state.totalPaginas) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Header Bar: Title + User greeting + Profile button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 20.dp, top = 16.dp, bottom = 12.dp)
            ) {
                Column {
                    Text(
                        text = "Trabajadores",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hola, ${state.usuarioActual}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.onIntent(DashboardUiIntent.LoadData(forceRefresh = true)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar trabajadores",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.onIntent(DashboardUiIntent.OnOpenPerfilMenu) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // 2. Search Bar: "Filtrar por DNI"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(WaraSurfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.filtroDni.isEmpty()) {
                            Text(
                                text = "Filtrar por DNI",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        BasicTextField(
                            value = state.filtroDni,
                            onValueChange = { viewModel.onIntent(DashboardUiIntent.OnSearchDniChanged(it)) },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            ),
                            cursorBrush = SolidColor(WaraPrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (state.filtroDni.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar filtro",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { viewModel.onIntent(DashboardUiIntent.OnClearSearchDni) }
                        )
                    }
                }
            }

            // 3. Filter Chips: "Activos" & "Todos"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Activos Chip
                FilterChipItem(
                    text = "Activos",
                    isSelected = state.soloActivos,
                    showCheck = true,
                    onClick = { viewModel.onIntent(DashboardUiIntent.OnFilterTabChanged(true)) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Todos Chip
                FilterChipItem(
                    text = "Todos",
                    isSelected = !state.soloActivos,
                    showCheck = false,
                    onClick = { viewModel.onIntent(DashboardUiIntent.OnFilterTabChanged(false)) }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Workers List / Loading / Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (state.isLoading && state.trabajadores.isEmpty()) {
                    CircularProgressIndicator(
                        color = WaraPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (state.trabajadores.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WaraDivider),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PeopleOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (state.filtroDni.isNotEmpty()) "Sin resultados" else "Sin trabajadores",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (state.filtroDni.isNotEmpty()) "No se encontraron trabajadores con DNI: ${state.filtroDni}" else "Aún no hay trabajadores registrados en la empresa.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 4.dp, bottom = 84.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(state.trabajadores, key = { _, item -> item.id }) { _, trabajador ->
                            TrabajadorListItem(
                                trabajador = trabajador,
                                onMoreClick = { viewModel.onIntent(DashboardUiIntent.OnOpenAccionesMenu(trabajador)) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet: Perfil
    if (state.isPerfilSheetVisible) {
        MenuPerfilBottomSheet(
            usuario = state.usuarioActual,
            onDismissRequest = { viewModel.onIntent(DashboardUiIntent.OnClosePerfilMenu) },
            onLogoutClick = { viewModel.onIntent(DashboardUiIntent.OnLogout) }
        )
    }

    // Modal Bottom Sheet: Acciones de Trabajador
    if (state.isAccionesSheetVisible) {
        MenuAccionesBottomSheet(
            onDismissRequest = { viewModel.onIntent(DashboardUiIntent.OnCloseAccionesMenu) },
            onEditClick = { viewModel.onIntent(DashboardUiIntent.OnEditTrabajadorSelected) },
            onDeleteClick = { viewModel.onIntent(DashboardUiIntent.OnDeleteTrabajadorRequested) }
        )
    }

    // Delete Confirmation Dialog
    if (state.isDeleteDialogVisible && state.selectedTrabajador != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(DashboardUiIntent.OnDismissDeleteDialog) },
            title = { Text(text = "Dar de baja") },
            text = {
                Text(
                    text = "¿Está seguro de que desea dar de baja al trabajador ${state.selectedTrabajador?.nombreCompleto} (DNI: ${state.selectedTrabajador?.dni})?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onIntent(DashboardUiIntent.OnConfirmDeleteTrabajador) }
                ) {
                    Text(text = "Dar de baja", color = WaraError)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onIntent(DashboardUiIntent.OnDismissDeleteDialog) }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    showCheck: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) WaraPrimaryContainer else Color.Transparent
    val border = if (isSelected) Color.Transparent else WaraOutline.copy(alpha = 0.5f)
    val contentColor = if (isSelected) WaraOnPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = border, shape = RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        if (showCheck && isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun TrabajadorListItem(
    trabajador: Trabajador,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, WaraDivider),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable { onMoreClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            WorkerAvatar(
                initials = trabajador.iniciales,
                size = 46.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trabajador.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Badge DNI
                    Surface(
                        color = WaraSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "DNI ${trabajador.dni}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Badge Edad
                    Surface(
                        color = WaraSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${trabajador.edad} años",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onMoreClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Acciones",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
