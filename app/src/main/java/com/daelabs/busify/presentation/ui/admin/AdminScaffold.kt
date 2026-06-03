package com.daelabs.busify.presentation.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daelabs.busify.domain.model.LoggedUser
import com.daelabs.busify.theme.*
import kotlinx.coroutines.launch

data class AdminNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

val ADMIN_NAV_ITEMS = listOf(
    AdminNavItem("Dashboard", Icons.Default.Dashboard, "admin/dashboard"),
    AdminNavItem("Rutas", Icons.Default.AltRoute, "admin/rutas"),
    AdminNavItem("Buses", Icons.Default.DirectionsBus, "admin/buses"),
    AdminNavItem("Choferes", Icons.Default.Person, "admin/choferes"),
    AdminNavItem("Viajes", Icons.Default.Map, "admin/viajes"),
    AdminNavItem("Usuarios", Icons.Default.People, "admin/usuarios"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    currentRoute: String,
    user: LoggedUser?,
    onNavClick: (String) -> Unit,
    onStoreClick: () -> Unit,
    onLogout: () -> Unit,
    title: String,
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                currentRoute = currentRoute,
                user = user,
                onNavClick = { route ->
                    scope.launch { drawerState.close() }
                    onNavClick(route)
                },
                onStoreClick = {
                    scope.launch { drawerState.close() }
                    onStoreClick()
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    onLogout()
                },
            )
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = TextPrimary)
                        }
                    },
                    actions = {
                        TextButton(onClick = onStoreClick) {
                            Text(
                                "← Inicio",
                                color = Accent,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
                )
            },
            containerColor = Background,
            content = content,
        )
    }
}

@Composable
private fun AdminDrawerContent(
    currentRoute: String,
    user: LoggedUser?,
    onNavClick: (String) -> Unit,
    onStoreClick: () -> Unit,
    onLogout: () -> Unit,
) {
    ModalDrawerSheet(
        drawerContainerColor = Surface,
        modifier = Modifier.width(280.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface2)
                .padding(24.dp),
        ) {
            Text(
                text = "Busify",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Accent,
            )
            Text(
                text = "Panel de administración",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }

        HorizontalDivider(color = Border, thickness = 0.5.dp)

        if (user != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(Accent, AccentLight)
                            ),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = user.username.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                        color = AccentOnDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                    )
                    Surface(
                        color = Accent.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.extraSmall,
                    ) {
                        Text(
                            text = "Staff",
                            color = Accent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Border, thickness = 0.5.dp)
        Spacer(Modifier.height(8.dp))

        ADMIN_NAV_ITEMS.forEach { item ->
            val isSelected = currentRoute == item.route || currentRoute.startsWith("${item.route}/")
            NavigationDrawerItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Accent else TextSecondary,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) Accent else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                },
                selected = isSelected,
                onClick = { onNavClick(item.route) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Accent.copy(alpha = 0.12f),
                    unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
            )
        }

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio Publico", tint = TextSecondary) },
            label = {
                Text(text = "Ir a Inicio Público", color = TextSecondary)
            },
            selected = false,
            onClick = onStoreClick,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        )

        Spacer(Modifier.weight(1f))
        HorizontalDivider(color = Border, thickness = 0.5.dp)

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Logout, contentDescription = "Salir", tint = Error) },
            label = {
                Text("Cerrar sesión", color = Error, fontWeight = FontWeight.SemiBold)
            },
            selected = false,
            onClick = onLogout,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}