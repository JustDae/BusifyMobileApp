package com.daelabs.busify.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.daelabs.busify.presentation.components.LoadingScreen
import com.daelabs.busify.presentation.ui.auth.LoginScreen
import com.daelabs.busify.presentation.ui.auth.RegisterScreen
import com.daelabs.busify.presentation.ui.uipublic.catalog.CatalogScreen
import com.daelabs.busify.presentation.ui.uipublic.home.HomeScreen
import com.daelabs.busify.presentation.viewmodel.AuthViewModel
import com.daelabs.busify.presentation.viewmodel.MonitoreoViewModel

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    monitoreoViewModel: MonitoreoViewModel = hiltViewModel(),
) {
    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()

    if (isCheckingSession) {
        LoadingScreen("Iniciando Busify Central...")
        return
    }

    NavGraphContent(
        authViewModel = authViewModel,
        monitoreoViewModel = monitoreoViewModel,
    )
}

@Composable
private fun NavGraphContent(
    authViewModel: AuthViewModel,
    monitoreoViewModel: MonitoreoViewModel,
) {
    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isStaff by authViewModel.isStaff.collectAsState()
    val busesCount by monitoreoViewModel.totalBusesMonitoreados.collectAsState()

    val startDestination = remember {
        when {
            !isAuthenticated -> Screen.Login.route
            isStaff          -> Screen.AdminDashboard.route
            else             -> Screen.Home.route
        }
    }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Catalog.route,
        Screen.Orders.route,
        Screen.Profile.route,
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    busesCount = busesCount,
                    onMonitoreoClick = { navController.navigate(Screen.Cart.route) },
                )
            }
        },
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { staff ->
                        val dest = if (staff) Screen.AdminDashboard.route else Screen.Home.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    viewModel = authViewModel,
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { staff ->
                        val dest = if (staff) Screen.AdminDashboard.route else Screen.Home.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                    viewModel = authViewModel,
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onRutaClick = { id -> navController.navigate("ruta/$id") },
                    onCatalogClick = { navController.navigate(Screen.Catalog.route) },
                )
            }

            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onRutaClick = { id -> navController.navigate("ruta/$id") },
                )
            }

            composable(
                route = "ruta/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) {
                LoadingScreen("Detalle Técnico de Ruta — Módulo 5")
            }

            composable(Screen.Cart.route) {
                ScreenWithLogout(
                    title = "Consola de Monitoreo de Unidades — Módulo 5",
                    onLogout = { authViewModel.logout() },
                )
            }

            composable(Screen.Orders.route) {
                ScreenWithLogout(
                    title = "Historial de Despachos y Frecuencias — Módulo 6",
                    onLogout = { authViewModel.logout() },
                )
            }

            composable(Screen.Profile.route) {
                ScreenWithLogout(
                    title = "Perfil del Operador / Conductor — Módulo 6",
                    onLogout = { authViewModel.logout() },
                ) {
                    LoadingScreen("Verificando credenciales de conducción...")
                }
            }

            composable(Screen.AdminDashboard.route) {
                ScreenWithLogout(
                    title = "Consola Administrativa de Operaciones Globales — Módulo 8",
                    onLogout = { authViewModel.logout() },
                )
            }
        }
    }
}

@Composable
fun ScreenWithLogout(
    title: String,
    onLogout: () -> Unit,
    content: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onLogout) {
            Text("Cerrar sesión remota")
        }
    }
}