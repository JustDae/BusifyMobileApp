package com.daelabs.busify.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Catalog : Screen("catalog")
    object Cart : Screen("cart")
    object Orders : Screen("orders")
    object Profile : Screen("profile")
    
    object AdminDashboard : Screen("admin/dashboard")
    
    object AdminRutas : Screen("admin/rutas")
    object AdminRutaEdit : Screen("admin/rutas/edit/{id}") {
        fun createRoute(id: Int?) = if (id == null) "admin/rutas/edit/-1" else "admin/rutas/edit/$id"
    }
    
    object AdminBuses : Screen("admin/buses")
    object AdminBusEdit : Screen("admin/buses/edit/{id}") {
        fun createRoute(id: Int?) = if (id == null) "admin/buses/edit/-1" else "admin/buses/edit/$id"
    }

    object AdminChoferes : Screen("admin/choferes")
    object AdminChoferEdit : Screen("admin/choferes/edit/{id}") {
        fun createRoute(id: Int?) = if (id == null) "admin/choferes/edit/-1" else "admin/choferes/edit/$id"
    }
    
    object AdminViajes : Screen("admin/viajes")
    object AdminViajeEdit : Screen("admin/viajes/edit/{id}") {
        fun createRoute(id: Int?) = if (id == null) "admin/viajes/edit/-1" else "admin/viajes/edit/$id"
    }

    object AdminUsuarios : Screen("admin/usuarios")
    object AdminUsuarioEdit : Screen("admin/usuarios/edit/{id}") {
        fun createRoute(id: Int?) = if (id == null) "admin/usuarios/edit/-1" else "admin/usuarios/edit/$id"
    }
}