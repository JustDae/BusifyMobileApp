package com.daelabs.busify.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val badgeCount: Int = 0,
)

@Composable
fun BottomNavBar(
    navController: NavController,
    busesCount: Int,
    isStaff: Boolean = false,
    onMonitoreoClick: () -> Unit,
) {
    val items = listOf(
        BottomNavItem(Screen.Home, "Inicio", Icons.Outlined.Home, Icons.Filled.Home),
        BottomNavItem(
            if (isStaff) Screen.AdminRutas else Screen.Catalog,
            "Rutas",
            Icons.Outlined.DirectionsBus,
            Icons.Filled.DirectionsBus
        ),
        BottomNavItem(Screen.Cart, "Monitoreo", Icons.Outlined.Analytics, Icons.Filled.Analytics, busesCount),
        BottomNavItem(Screen.Orders, "Historial", Icons.Outlined.History, Icons.Filled.History),
        BottomNavItem(Screen.Profile, "Perfil", Icons.Outlined.AccountCircle, Icons.Filled.AccountCircle),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.screen.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item.screen == Screen.Cart) {
                        onMonitoreoClick()
                    } else {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (item.badgeCount > 0) {
                        BadgedBox(badge = {
                            Badge(containerColor = MaterialTheme.colorScheme.error) {
                                Text(
                                    text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }) {
                            Icon(
                                imageVector = if (isSelected) item.iconSelected else item.icon,
                                contentDescription = item.label,
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.iconSelected else item.icon,
                            contentDescription = item.label,
                        )
                    }
                },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                ),
            )
        }
    }
}