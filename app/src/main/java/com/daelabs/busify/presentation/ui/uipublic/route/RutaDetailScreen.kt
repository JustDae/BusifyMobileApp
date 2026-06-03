package com.daelabs.busify.presentation.ui.uipublic.route

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.repository.RutaRepository
import com.daelabs.busify.presentation.components.LoadingScreen
import com.daelabs.busify.presentation.viewmodel.DespachoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RutaDetailUiState {
    data object Loading : RutaDetailUiState
    data class Success(val ruta: Ruta) : RutaDetailUiState
    data class Error(val message: String) : RutaDetailUiState
}

@HiltViewModel
class RutaDetailViewModel @Inject constructor(
    private val repository: RutaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<RutaDetailUiState>(RutaDetailUiState.Loading)
    val state: StateFlow<RutaDetailUiState> = _state.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = RutaDetailUiState.Loading
            repository.getRuta(id)
                .onSuccess { _state.value = RutaDetailUiState.Success(it) }
                .onFailure { _state.value = RutaDetailUiState.Error(it.message ?: "Error de conexión") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutaDetailScreen(
    rutaId: Int,
    onBack: () -> Unit,
    despachoViewModel: DespachoViewModel,
    viewModel: RutaDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(rutaId) { viewModel.load(rutaId) }

    when (val s = state) {
        is RutaDetailUiState.Loading -> LoadingScreen("Cargando detalles de ruta...")
        is RutaDetailUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = s.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.load(rutaId) }) {
                        Text("Reintentar")
                    }
                }
            }
        }
        is RutaDetailUiState.Success -> RutaDetailContent(
            ruta = s.ruta,
            onBack = onBack,
            despachoViewModel = despachoViewModel,
        )
    }
}

@Composable
private fun RutaDetailContent(
    ruta: Ruta,
    onBack: () -> Unit,
    despachoViewModel: DespachoViewModel,
) {
    var unidadesADespachar by remember { mutableIntStateOf(1) }
    var asignado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            if (ruta.mapaSnippetUrl != null) {
                AsyncImage(
                    model = ruta.mapaSnippetUrl,
                    contentDescription = ruta.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surfaceVariant)
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) { 
                    Icon(
                        Icons.Default.Map, 
                        contentDescription = null, 
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    ) 
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape),
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Black)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = ruta.cooperativaName?.uppercase() ?: "LÍNEA",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = ruta.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tarifa por Unidad",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "$${"%.2f".format(ruta.tarifa)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Surface(
                        color = if (ruta.hasBusesActivos) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = CircleShape
                    ) {
                        Text(
                            text = if (ruta.hasBusesActivos) "● EN SERVICIO" else "● FUERA DE SERVICIO",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (ruta.hasBusesActivos) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Información del Servicio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoCard(
                        icon = Icons.Default.DirectionsBus,
                        label = "Flota Total",
                        value = "${ruta.totalBuses} Unidades",
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        icon = Icons.Default.People,
                        label = "Capacidad",
                        value = "${ruta.maxCapacidadBuses} Pasaj.",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(32.dp))
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Consola de Despacho",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configure las unidades para monitoreo en vivo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(Modifier.height(16.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "Unidades a Monitorear", 
                            style = MaterialTheme.typography.bodyMedium, 
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (unidadesADespachar > 1) unidadesADespachar-- },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Disminuir", modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = unidadesADespachar.toString(), 
                                style = MaterialTheme.typography.titleMedium, 
                                fontWeight = FontWeight.ExtraBold, 
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            FilledIconButton(
                                onClick = { if (unidadesADespachar < 15) unidadesADespachar++ },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        despachoViewModel.addRutaALaCola(ruta, unidadesADespachar)
                        asignado = true
                    },
                    enabled = !asignado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (asignado) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(if (asignado) Icons.Default.CheckCircle else Icons.Default.AddModerator, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (asignado) "Línea en Cola de Monitoreo" else "Asignar a Consola",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}