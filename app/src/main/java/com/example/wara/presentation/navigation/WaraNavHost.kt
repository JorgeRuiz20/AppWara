package com.example.wara.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wara.di.AppContainer
import com.example.wara.presentation.auth.login.LoginScreen
import com.example.wara.presentation.auth.login.LoginViewModel
import com.example.wara.presentation.auth.register.RegisterScreen
import com.example.wara.presentation.auth.register.RegisterViewModel
import com.example.wara.presentation.dashboard.DashboardScreen
import com.example.wara.presentation.dashboard.DashboardUiIntent
import com.example.wara.presentation.dashboard.DashboardViewModel
import com.example.wara.presentation.trabajador.add.AddTrabajadorScreen
import com.example.wara.presentation.trabajador.add.AddTrabajadorViewModel
import com.example.wara.presentation.trabajador.edit.EditTrabajadorScreen
import com.example.wara.presentation.trabajador.edit.EditTrabajadorViewModel
import com.example.wara.ui.theme.WaraPrimary
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun WaraNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val token = appContainer.sessionDataStore.tokenFlow.firstOrNull()
        startDestination = if (!token.isNullOrBlank()) {
            Screen.Dashboard.route
        } else {
            Screen.Login.route
        }
    }

    if (startDestination == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CircularProgressIndicator(
                color = WaraPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!,
        modifier = modifier
    ) {
        composable(Screen.Login.route) { backStackEntry ->
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.provideFactory(
                    appContainer.loginUseCase,
                    appContainer.sessionDataStore
                )
            )
            val authMessage by backStackEntry.savedStateHandle.getStateFlow<String?>("auth_message", null).collectAsState()

            LoginScreen(
                viewModel = loginViewModel,
                externalMessage = authMessage,
                onClearExternalMessage = { backStackEntry.savedStateHandle["auth_message"] = null },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModel.provideFactory(appContainer.registerUseCase)
            )
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("auth_message", message.ifBlank { "¡Usuario registrado exitosamente! Inicia sesión para continuar." })
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Dashboard.route) { backStackEntry ->
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.provideFactory(
                    appContainer.getSavedSessionUseCase,
                    appContainer.getUsuarioActualUseCase,
                    appContainer.getTrabajadoresUseCase,
                    appContainer.eliminarTrabajadorUseCase,
                    appContainer.logoutUseCase
                )
            )

            val refreshRequired by backStackEntry.savedStateHandle.getStateFlow("refresh_required", false).collectAsState()
            val dashboardMessage by backStackEntry.savedStateHandle.getStateFlow<String?>("dashboard_message", null).collectAsState()

            LaunchedEffect(refreshRequired) {
                if (refreshRequired) {
                    dashboardViewModel.onIntent(DashboardUiIntent.LoadData(forceRefresh = true))
                    backStackEntry.savedStateHandle["refresh_required"] = false
                }
            }

            DashboardScreen(
                viewModel = dashboardViewModel,
                externalMessage = dashboardMessage,
                onClearExternalMessage = { backStackEntry.savedStateHandle["dashboard_message"] = null },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToAddTrabajador = {
                    navController.navigate(Screen.AddTrabajador.route)
                },
                onNavigateToEditTrabajador = { workerId ->
                    navController.navigate(Screen.EditTrabajador.createRoute(workerId))
                }
            )
        }

        composable(Screen.AddTrabajador.route) {
            val addViewModel: AddTrabajadorViewModel = viewModel(
                factory = AddTrabajadorViewModel.provideFactory(appContainer.crearTrabajadorUseCase)
            )
            AddTrabajadorScreen(
                viewModel = addViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWorkerAdded = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_required", true)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("dashboard_message", message.ifBlank { "Trabajador creado exitosamente." })
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditTrabajador.route,
            arguments = listOf(
                navArgument("workerId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getInt("workerId") ?: 0
            val editViewModel: EditTrabajadorViewModel = viewModel(
                factory = EditTrabajadorViewModel.provideFactory(
                    appContainer.getTrabajadorByIdUseCase,
                    appContainer.actualizarTrabajadorUseCase
                )
            )
            EditTrabajadorScreen(
                workerId = workerId,
                viewModel = editViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWorkerUpdated = { message ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh_required", true)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("dashboard_message", message.ifBlank { "Trabajador actualizado exitosamente." })
                    navController.popBackStack()
                }
            )
        }
    }
}
