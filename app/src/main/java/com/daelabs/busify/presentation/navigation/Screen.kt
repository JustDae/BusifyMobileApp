package com.daelabs.busify.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Catalog : Screen("catalog")
    object Cart : Screen("cart")
    object Orders : Screen("orders")
    object Profile : Screen("profile")
    object AdminDashboard : Screen("admin_dashboard")
}