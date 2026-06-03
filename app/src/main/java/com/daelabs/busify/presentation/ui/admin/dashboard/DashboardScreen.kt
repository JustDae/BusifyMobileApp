package com.daelabs.busify.presentation.ui.admin.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daelabs.busify.domain.model.ViajeStatus
import com.daelabs.busify.presentation.components.LoadingScreen
import com.daelabs.busify.presentation.components.viajeStatusColor
import com.daelabs.busify.presentation.viewmodel.DashboardUiState
import com.daelabs.busify.presentation.viewmodel.DashboardViewModel
import com.daelabs.busify.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()

    when (val s = state) {
        is DashboardUiState.Loading ->
            LoadingScreen("Cargando dashboard...")
        is DashboardUiState.Error -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠️ ${s.message}", color = Error)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = viewModel::load,
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Text("Reintentar", color = AccentOnDark)
                    }
                }
            }
        }
        is DashboardUiState.Success ->
            DashboardContent(
                stats = s.stats,
                lastUpdated = lastUpdated,
                onNavigate = onNavigate,
                onRefresh = viewModel::load,
            )
    }
}

@Composable
private fun DashboardContent(
    stats: com.daelabs.busify.presentation.viewmodel.DashboardStats,
    lastUpdated: Long,
    onNavigate: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeStr = if (lastUpdated > 0) timeFmt.format(Date(lastUpdated)) else "—"

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "Actualizado: $timeStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Accent)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title = "Buses activos",
                    value = stats.totalActiveBuses.toString(),
                    subtitle = if (stats.inactiveBuses > 0)
                        "${stats.inactiveBuses} inactivos" else null,
                    icon = Icons.Default.DirectionsBus,
                    color = Accent,
                    hasAlert = stats.inactiveBuses > 0,
                    onClick = { onNavigate("admin/buses") },
                    modifier = Modifier.weight(1f),
                )
                KpiCard(
                    title = "Rutas activas",
                    value = stats.activeRutas.toString(),
                    subtitle = "${stats.totalRutas} total",
                    icon = Icons.Default.AltRoute,
                    color = Info,
                    onClick = { onNavigate("admin/rutas") },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title = "Viajes totales",
                    value = stats.totalViajes.toString(),
                    icon = Icons.Default.Map,
                    color = Success,
                    onClick = { onNavigate("admin/viajes") },
                    modifier = Modifier.weight(1f),
                )
                KpiCard(
                    title = "Usuarios activos",
                    value = stats.activeUsers.toString(),
                    subtitle = "${stats.totalUsers} registrados",
                    icon = Icons.Default.People,
                    color = Warning,
                    onClick = { onNavigate("admin/usuarios") },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title = "Choferes activos",
                    value = stats.availableChoferes.toString(),
                    subtitle = "${stats.totalChoferes} total",
                    icon = Icons.Default.Person,
                    color = Info,
                    onClick = { onNavigate("admin/choferes") },
                    modifier = Modifier.weight(1f),
                )
                KpiCard(
                    title = "Velocidad media",
                    value = "${"%.1f".format(stats.avgSpeed)} km/h",
                    icon = Icons.Default.Speed,
                    color = Accent,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title = "Personal staff",
                    value = stats.staffUsers.toString(),
                    icon = Icons.Default.AdminPanelSettings,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        if (stats.viajesByStatus.isNotEmpty()) {
            item {
                Surface(
                    color = Surface,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Viajes por estado",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                            )
                            TextButton(onClick = { onNavigate("admin/viajes") }) {
                                Text(
                                    "Ver todos", color = Accent,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        val total = stats.totalViajes.coerceAtLeast(1)
                        stats.viajesByStatus.entries.forEach { (statusValue, count) ->
                            val status = ViajeStatus.fromValue(statusValue)
                            val color = viajeStatusColor(status)
                            val pct = (count.toFloat() / total).coerceIn(0.02f, 1f)

                            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = status.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                    )
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = color,
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(7.dp)
                                        .background(Surface2, MaterialTheme.shapes.extraSmall),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pct)
                                            .fillMaxHeight()
                                            .background(color, MaterialTheme.shapes.extraSmall),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Surface(
                color = Surface,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Warning,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Buses inactivos",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                            )
                        }
                        TextButton(onClick = { onNavigate("admin/buses") }) {
                            Text(
                                "Gestionar", color = Accent,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    if (stats.alertBuses.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "✅ Todos los buses operativos", color = Success,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Spacer(Modifier.height(8.dp))
                        stats.alertBuses.forEach { bus ->
                            Surface(
                                onClick = { onNavigate("admin/buses") },
                                color = Surface2,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Bus ${bus.numeroBus} - ${bus.placa}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                    )
                                    Surface(
                                        color = Error.copy(alpha = 0.15f),
                                        shape = MaterialTheme.shapes.extraSmall,
                                    ) {
                                        Text(
                                            text = "Inactivo",
                                            color = Error,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Surface(
                color = Surface,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "⚡ Acciones rápidas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(
                            listOf(
                                Triple("+ Ruta", Info, "admin/rutas"),
                                Triple("+ Bus", Accent, "admin/buses"),
                                Triple("+ Chofer", Info, "admin/choferes"),
                                Triple("Viajes", Success, "admin/viajes"),
                                Triple("Usuarios", Warning, "admin/usuarios"),
                            )
                        ) { (label, color, route) ->
                            Surface(
                                onClick = { onNavigate(route) },
                                color = color.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(
                                    text = label,
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}