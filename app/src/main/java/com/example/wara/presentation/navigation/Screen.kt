package com.example.wara.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object AddTrabajador : Screen("add_trabajador")
    object EditTrabajador : Screen("edit_trabajador/{workerId}") {
        fun createRoute(workerId: Int): String = "edit_trabajador/$workerId"
    }
}
