package com.daelabs.busify.presentation.ui.client.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.model.ViajeStatus
import com.daelabs.busify.presentation.components.StatusBadge
import com.daelabs.busify.presentation.components.LoadingScreen
import com.daelabs.busify.presentation.viewmodel.HistorialDespachoViewModel

private val FILTROS_ESTADO = listOf(
    "" to "Todos",
    ViajeStatus.PENDING.name to "Programados",
    ViajeStatus.EN_PROGRESO.name to "En Ruta",
    ViajeStatus.COMPLETADO.name to "Terminados",
    ViajeStatus.CANCELADO.name to "Cancelados"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onViajeClick: (Int) -> Unit,
    viewModel: HistorialDespachoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 2 && !state.isLoadingMore && state.hasMore
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Registro de Frecuencias", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(if (state.isLoading) "Sincronizando..." else "${state.total} despachos controlados", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                }
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FILTROS_ESTADO) { (value, label) ->
                        FilterChip(
                            selected = state.statusFilter == value,
                            onClick = { viewModel.setStatusFilter(value) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }

        if (state.isLoading && state.viajes.isEmpty()) {
            LoadingScreen("Leyendo bitácora satelital...")
        } else if (state.viajes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("📋 Sin registros de viajes para este estado", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.viajes, key = { it.id }) { viaje ->
                    ViajeCard(viaje = viaje, onClick = { onViajeClick(viaje.id) })
                }
            }
        }
    }
}

@Composable
fun ViajeCard(viaje: Viaje, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Código Despacho #${viaje.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Bus Placa: ${viaje.busPlaca}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                StatusBadge(status = viaje.status)
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${viaje.numPasajerosTotal} pasajeros reportados", style = MaterialTheme.typography.bodySmall)
                Text("$${"%.2f".format(viaje.tarifaTotalRecaudada)} Recaudado", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}