package com.daelabs.busify.presentation.ui.client.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.model.ViajeStatus
import com.daelabs.busify.presentation.components.LoadingScreen
import com.daelabs.busify.presentation.components.StatusBadge
import com.daelabs.busify.presentation.viewmodel.ViajeDetailUiState
import com.daelabs.busify.presentation.viewmodel.MonitoreoViewModel

private val PASOS_RUTA = listOf(
    ViajeStatus.PENDING,
    ViajeStatus.ON_ROUTE,
    ViajeStatus.COMPLETED
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    viajeId: Int,
    onBack: () -> Unit,
    viewModel: MonitoreoViewModel = hiltViewModel(),
) {
    val state by viewModel.detailState.collectAsState()
    LaunchedEffect(viajeId) { viewModel.loadViajeDetail(viajeId) }

    when (val s = state) {
        is ViajeDetailUiState.Loading -> LoadingScreen("Cargando información de tu viaje...")
        is ViajeDetailUiState.Error -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Boleto #${viajeId}") },
                        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
                    )
                }
            ) { paddingValues -> Box(Modifier.padding(paddingValues).fillMaxSize(), contentAlignment = Alignment.Center) { Text(s.message) } }
        }
        is ViajeDetailUiState.Success -> ViajeDetailContent(viaje = s.viaje, onBack = onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViajeDetailContent(viaje: Viaje, onBack: () -> Unit) {
    val pasoActual = PASOS_RUTA.indexOf(viaje.status).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información del Viaje") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { StatusBadge(viaje.status, modifier = Modifier.padding(end = 16.dp)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProgresoViajeBar(steps = PASOS_RUTA, currentStep = pasoActual)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detalles de tu Boleto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Ruta: ${viaje.rutaNombre}")
                    Text("Unidad: ${viaje.busPlaca}")
                    Text("Pasajeros: ${viaje.numPasajerosTotal}")
                    Text("Total Pagado: $${String.format(Locale.US, "%.2f", viaje.tarifaTotalRecaudada)}")
                }
            }
        }
    }
}

@Composable
private fun ProgresoViajeBar(steps: List<ViajeStatus>, currentStep: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Estado del Viaje", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                steps.forEachIndexed { index, step ->
                    val completado = index <= currentStep
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(if (completado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (completado) "✓" else "${index + 1}", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp)
                    }
                    if (index < steps.lastIndex) {
                        Box(modifier = Modifier.weight(1f).height(2.dp).background(if (index < currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant))
                    }
                }
            }
        }
    }
}