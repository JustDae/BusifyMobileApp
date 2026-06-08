package com.daelabs.busify.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.daelabs.busify.presentation.ui.admin.AdminScaffold
import com.daelabs.busify.presentation.ui.admin.dashboard.DashboardScreen
import com.daelabs.busify.presentation.ui.admin.rutas.AdminRutaFormScreen
import com.daelabs.busify.presentation.ui.admin.rutas.AdminRutaListScreen
import com.daelabs.busify.presentation.ui.admin.buses.AdminBusFormScreen
import com.daelabs.busify.presentation.ui.admin.buses.AdminBusListScreen
import com.daelabs.busify.presentation.ui.admin.choferes.AdminChoferFormScreen
import com.daelabs.busify.presentation.ui.admin.choferes.AdminChoferListScreen
import com.daelabs.busify.presentation.ui.admin.viajes.AdminViajeFormScreen
import com.daelabs.busify.presentation.ui.admin.viajes.AdminViajeListScreen
import com.daelabs.busify.presentation.ui.admin.usuarios.AdminUsuarioFormScreen
import com.daelabs.busify.presentation.ui.admin.usuarios.AdminUsuarioListScreen
import com.daelabs.busify.presentation.ui.auth.LoginScreen
import com.daelabs.busify.presentation.ui.auth.RegisterScreen
import com.daelabs.busify.presentation.ui.uipublic.catalog.CatalogScreen
import com.daelabs.busify.presentation.ui.uipublic.home.HomeScreen
import com.daelabs.busify.presentation.ui.uipublic.route.RutaDetailScreen
import com.daelabs.busify.presentation.ui.uipublic.cart.DespachoBottomSheet
import com.daelabs.busify.presentation.ui.client.orders.OrdersScreen
import com.daelabs.busify.presentation.ui.client.orders.OrderDetailScreen
import com.daelabs.busify.presentation.ui.client.profile.ProfileScreen
import com.daelabs.busify.presentation.viewmodel.AuthViewModel
import com.daelabs.busify.presentation.viewmodel.MonitoreoViewModel
import com.daelabs.busify.presentation.viewmodel.DespachoViewModel

@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    monitoreoViewModel: MonitoreoViewModel = hiltViewModel(),
    despachoViewModel: DespachoViewModel = hiltViewModel(),
) {
    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()

    if (isCheckingSession) {
        LoadingScreen("Iniciando Busify Central...")
        return
    }

    NavGraphContent(
        authViewModel = authViewModel,
        monitoreoViewModel = monitoreoViewModel,
        despachoViewModel = despachoViewModel,
    )
}

@Composable
private fun NavGraphContent(
    authViewModel: AuthViewModel,
    monitoreoViewModel: MonitoreoViewModel,
    despachoViewModel: DespachoViewModel,
) {
    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isStaff by authViewModel.isStaff.collectAsState()
    val busesCount by monitoreoViewModel.totalBusesMonitoreados.collectAsState()

    var mostrarConsolaDespacho by remember { mutableStateOf(false) }
    var trackingViajeId by remember { mutableStateOf<Int?>(null) }

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
                    isStaff = isStaff,
                    onMonitoreoClick = { mostrarConsolaDespacho = true },
                )
            }
        },
    ) { innerPadding ->

        if (mostrarConsolaDespacho) {
            DespachoBottomSheet(
                viewModel = despachoViewModel,
                isOperadorAutenticado = isAuthenticated,
                onDismiss = { mostrarConsolaDespacho = false },
                onAuthRequired = {
                    mostrarConsolaDespacho = false
                    navController.navigate(Screen.Login.route)
                },
                onDespachoConfirmado = { viajeId ->
                    trackingViajeId = viajeId
                    mostrarConsolaDespacho = false
                    navController.navigate("orders/$viajeId")
                },
            )
        }

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
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                RutaDetailScreen(
                    rutaId = id,
                    onBack = { navController.popBackStack() },
                    despachoViewModel = despachoViewModel,
                )
            }

            composable(Screen.Orders.route) {
                OrdersScreen(
                    onViajeClick = { id -> navController.navigate("orders/$id") }
                )
            }

            composable(
                route = "orders/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                OrderDetailScreen(
                    viajeId = id,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.AdminDashboard.route) {
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                AdminScaffold(
                    currentRoute = Screen.AdminDashboard.route,
                    user         = authViewModel.currentUser.collectAsState().value,
                    title        = "Dashboard",
                    onNavClick   = { route -> navController.navigate(route) {
                        launchSingleTop = true
                        restoreState    = true
                    }},
                    onStoreClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.AdminDashboard.route) { inclusive = false }
                        }
                    },
                    onLogout     = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        DashboardScreen(onNavigate = { route -> navController.navigate(route) })
                    }
                }
            }

            composable(Screen.AdminRutas.route) {
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                AdminScaffold(
                    currentRoute = Screen.AdminRutas.route,
                    user         = authViewModel.currentUser.collectAsState().value,
                    title        = "Rutas",
                    onNavClick   = { r -> navController.navigate(r) { launchSingleTop = true } },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout     = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AdminRutaListScreen(
                            onEditRuta = { id -> 
                                navController.navigate(Screen.AdminRutaEdit.createRoute(id)) 
                            }
                        )
                    }
                }
            }

            composable(
                route = Screen.AdminRutaEdit.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                val id = backStackEntry.arguments?.getInt("id")
                val actualId = if (id == -1) null else id
                AdminRutaFormScreen(
                    rutaId = actualId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminBuses.route) {
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                AdminScaffold(
                    currentRoute = Screen.AdminBuses.route,
                    user         = authViewModel.currentUser.collectAsState().value,
                    title        = "Buses",
                    onNavClick   = { r -> navController.navigate(r) { launchSingleTop = true } },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout     = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AdminBusListScreen(
                            onEditBus = { id -> 
                                navController.navigate(Screen.AdminBusEdit.createRoute(id)) 
                            }
                        )
                    }
                }
            }

            composable(
                route = Screen.AdminBusEdit.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                val id = backStackEntry.arguments?.getInt("id")
                val actualId = if (id == -1) null else id
                AdminBusFormScreen(
                    busId = actualId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminChoferes.route) {
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                AdminScaffold(
                    currentRoute = Screen.AdminChoferes.route,
                    user         = authViewModel.currentUser.collectAsState().value,
                    title        = "Choferes",
                    onNavClick   = { r -> navController.navigate(r) { launchSingleTop = true } },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout     = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AdminChoferListScreen(
                            onEditChofer = { id -> 
                                navController.navigate(Screen.AdminChoferEdit.createRoute(id)) 
                            }
                        )
                    }
                }
            }

            composable(
                route = Screen.AdminChoferEdit.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                val id = backStackEntry.arguments?.getInt("id")
                val actualId = if (id == -1) null else id
                AdminChoferFormScreen(
                    choferId = actualId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminViajes.route) {
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                AdminScaffold(
                    currentRoute = Screen.AdminViajes.route,
                    user         = authViewModel.currentUser.collectAsState().value,
                    title        = "Viajes",
                    onNavClick   = { r -> navController.navigate(r) { launchSingleTop = true } },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout     = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AdminViajeListScreen(
                            onEditViaje = { id -> 
                                navController.navigate(Screen.AdminViajeEdit.createRoute(id)) 
                            }
                        )
                    }
                }
            }

            composable(
                route = Screen.AdminViajeEdit.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                val id = backStackEntry.arguments?.getInt("id")
                val actualId = if (id == -1) null else id
                AdminViajeFormScreen(
                    viajeId = actualId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminUsuarios.route) {
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                AdminScaffold(
                    currentRoute = Screen.AdminUsuarios.route,
                    user         = authViewModel.currentUser.collectAsState().value,
                    title        = "Usuarios",
                    onNavClick   = { r -> navController.navigate(r) { launchSingleTop = true } },
                    onStoreClick = { navController.navigate(Screen.Home.route) },
                    onLogout     = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AdminUsuarioListScreen(
                            onEditUser = { id -> 
                                navController.navigate(Screen.AdminUsuarioEdit.createRoute(id)) 
                            }
                        )
                    }
                }
            }

            composable(
                route = Screen.AdminUsuarioEdit.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                if (!isStaff) {
                    LaunchedEffect(Unit) { navController.navigate(Screen.Home.route) { popUpTo(0) } }
                    return@composable
                }
                val id = backStackEntry.arguments?.getInt("id")
                val actualId = if (id == -1) null else id
                AdminUsuarioFormScreen(
                    userId = actualId,
                    onNavigateBack = { navController.popBackStack() }
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