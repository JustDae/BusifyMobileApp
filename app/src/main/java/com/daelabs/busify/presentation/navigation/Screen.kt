package com.daelabs.busify.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object AdminDashboard : Screen("admin/dashboard")
}