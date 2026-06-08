package com.daelabs.busify.presentation.ui.client.orders

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.model.ViajeStatus
import com.daelabs.busify.presentation.components.StatusBadge
import com.daelabs.busify.presentation.components.LoadingScreen
import com.daelabs.busify.presentation.viewmodel.HistorialDespachoViewModel

private val FILTROS_ESTADO = listOf(
    "" to "Todos",
    ViajeStatus.SCHEDULED.value to "Próximos",
    ViajeStatus.ON_ROUTE.value to "En Viaje",
    ViajeStatus.COMPLETED.value to "Historial",
    ViajeStatus.CANCELLED.value to "Cancelados"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onViajeClick: (Int) -> Unit,
    viewModel: HistorialDespachoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val primaryGreen = Color(0xFF2EBD6B)

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

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F8F5))) {
        Surface(color = Color(0xFFF1F8F5)) {
            Column(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mis Viajes", 
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1A1A1A)
                        )
                        val totalPasajeros = remember(state.viajes) { 
                            state.viajes.sumOf { it.numPasajerosTotal } 
                        }
                        val totalRecaudado = remember(state.viajes) {
                            state.viajes.sumOf { it.tarifaTotalRecaudada }
                        }
                        val boletosCargados = state.viajes.size
                        Text(
                            text = if (state.isLoading && state.viajes.isEmpty()) "Sincronizando..." 
                                   else "$totalPasajeros ${if (totalPasajeros == 1) "pasajero" else "pasajeros"} • $${String.format(Locale.US, "%.2f", totalRecaudado)} en $boletosCargados ${if (boletosCargados == 1) "boleto" else "boletos"}${if (boletosCargados < state.total) " (de ${state.total})" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    val rotation = rememberInfiniteTransition(label = "refresh")
                        .animateFloat(
                            initialValue = 0f,
                            targetValue = if (state.isLoading) 360f else 0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "rotation"
                        )

                    IconButton(
                        onClick = viewModel::refresh,
                        enabled = !state.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refrescar",
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(26.dp)
                                .let { if (state.isLoading) it.rotate(rotation.value) else it }
                        )
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FILTROS_ESTADO) { (value, label) ->
                        val isSelected = state.statusFilter == value
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setStatusFilter(value) },
                            label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryGreen.copy(alpha = 0.15f),
                                selectedLabelColor = primaryGreen,
                                containerColor = Color.White,
                                labelColor = Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color(0xFFE0E0E0),
                                borderWidth = 1.dp,
                                selectedBorderColor = primaryGreen.copy(alpha = 0.3f),
                                selectedBorderWidth = 1.dp
                            )
                        )
                    }
                }
            }
        }

        if (state.isLoading && state.viajes.isEmpty()) {
            LoadingScreen("Cargando tus boletos...")
        } else if (state.error != null && state.viajes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("⚠️", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = state.error ?: "Error desconocido",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = viewModel::refresh,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        } else if (state.viajes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📋", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "No tienes boletos en este estado", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1A1A1A)
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().background(Color.White),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.viajes, key = { it.id }) { viaje ->
                    ViajeCard(viaje = viaje, onClick = { onViajeClick(viaje.id) }, primaryGreen = primaryGreen)
                }
            }
        }
    }
}

@Composable
fun ViajeCard(viaje: Viaje, onClick: () -> Unit, primaryGreen: Color) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = primaryGreen.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = primaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = viaje.rutaNombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Unidad: ${viaje.busPlaca} • ${viaje.numPasajerosTotal} ${if (viaje.numPasajerosTotal == 1) "pasajero" else "pasajeros"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(16.dp))
                StatusBadge(status = viaje.status)
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalActivity,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Boleto #${viaje.id} • $${String.format(Locale.US, "%.2f", viaje.tarifaTotalRecaudada)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Gestionar",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
            }
        }
    }
}
