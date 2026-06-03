package com.daelabs.busify.presentation.ui.admin.viajes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.presentation.viewmodel.admin.AdminViajesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminViajeListScreen(
    viewModel: AdminViajesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val statuses = listOf("PROGRAMADO", "EN_CURSO", "FINALIZADO", "CANCELADO")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (state.statusFilter == null) "Todos los Viajes" else "Estado: ${state.statusFilter}",
                style = MaterialTheme.typography.titleMedium
            )
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos") },
                        onClick = {
                            viewModel.onStatusFilterChange(null)
                            expanded = false
                        }
                    )
                    statuses.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                viewModel.onStatusFilterChange(status)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        if (state.isLoading && state.viajes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null && state.viajes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.viajes) { viaje ->
                    ViajeAdminItem(
                        viaje = viaje,
                        onUpdateStatus = { newStatus ->
                            viewModel.updateViajeStatus(viaje.id, newStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ViajeAdminItem(
    viaje: Viaje,
    onUpdateStatus: (String) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }
    val statuses = listOf("PROGRAMADO", "EN_CURSO", "FINALIZADO", "CANCELADO")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Viaje #${viaje.id}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Box {
                    StatusBadge(
                        status = viaje.status,
                        onClick = { showStatusMenu = true }
                    )
                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    onUpdateStatus(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bus: ${viaje.busPlaca}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = "Pasajeros: ${viaje.numPasajerosTotal}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Fecha: ${viaje.createdAt.take(10)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: com.daelabs.busify.domain.model.ViajeStatus,
    onClick: (() -> Unit)? = null
) {
    val color = when (status) {
        com.daelabs.busify.domain.model.ViajeStatus.EN_PROGRESO -> Color(0xFF22C55E)
        com.daelabs.busify.domain.model.ViajeStatus.PENDING -> Color(0xFF3B82F6)
        com.daelabs.busify.domain.model.ViajeStatus.COMPLETADO -> Color(0xFF6B7280)
        com.daelabs.busify.domain.model.ViajeStatus.CANCELADO -> Color(0xFFEF4444)
    }
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.extraSmall,
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}