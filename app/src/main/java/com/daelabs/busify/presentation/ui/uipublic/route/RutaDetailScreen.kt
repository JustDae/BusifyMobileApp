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
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.model.ViajeStatus
import com.daelabs.busify.domain.repository.RutaRepository
import com.daelabs.busify.domain.repository.ViajeRepository
import com.daelabs.busify.presentation.components.LoadingScreen
import com.daelabs.busify.presentation.viewmodel.DespachoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed interface RutaDetailUiState {
    data object Loading : RutaDetailUiState
    data class Success(val ruta: Ruta, val viajes: List<Viaje> = emptyList()) : RutaDetailUiState
    data class Error(val message: String) : RutaDetailUiState
}

@HiltViewModel
class RutaDetailViewModel @Inject constructor(
    private val repository: RutaRepository,
    private val viajeRepository: ViajeRepository
) : ViewModel() {

    private val _state = MutableStateFlow<RutaDetailUiState>(RutaDetailUiState.Loading)
    val state: StateFlow<RutaDetailUiState> = _state.asStateFlow()

    private val _isBuying = MutableStateFlow(false)
    val isBuying: StateFlow<Boolean> = _isBuying.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = RutaDetailUiState.Loading

            val rutaResult = repository.getRuta(id)
            val viajesResult = viajeRepository.getViajes(status = ViajeStatus.ON_ROUTE.value)

            rutaResult.onSuccess { ruta ->
                val viajesDeRuta = viajesResult.getOrNull()?.first?.filter { it.rutaId == id } ?: emptyList()
                _state.value = RutaDetailUiState.Success(ruta, viajesDeRuta)
            }.onFailure {
                _state.value = RutaDetailUiState.Error(it.message ?: "Error de conexión")
            }
        }
    }

    suspend fun comprarPasaje(viajeId: Int, cantidad: Int = 1): Int {
        if (cantidad <= 0) return 0
        _isBuying.value = true
        
        var successCount = 0
        
        repeat(cantidad) {
            val result = viajeRepository.comprarPasaje(viajeId, 1)
            if (result.isSuccess) {
                successCount++
                val updatedViaje = result.getOrNull()
                
                if (updatedViaje != null) {
                    val currentState = _state.value
                    if (currentState is RutaDetailUiState.Success) {
                        val updatedViajes = currentState.viajes.map {
                            if (it.id == viajeId) updatedViaje else it
                        }
                        _state.value = currentState.copy(viajes = updatedViajes)
                    }
                }
                kotlinx.coroutines.delay(150)
            }
        }

        _isBuying.value = false
        return successCount
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
            viajesActivos = s.viajes,
            onBack = onBack,
            despachoViewModel = despachoViewModel,
            viewModel = viewModel
        )
    }
}

@Composable
private fun RutaDetailContent(
    ruta: Ruta,
    viajesActivos: List<Viaje>,
    onBack: () -> Unit,
    despachoViewModel: DespachoViewModel,
    viewModel: RutaDetailViewModel
) {
    var unidadesADespachar by remember { mutableIntStateOf(1) }
    var asignado by remember { mutableStateOf(false) }
    val cantidadesSeleccionadas = remember { mutableStateMapOf<Int, Int>() }
    val isBuying by viewModel.isBuying.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
                        .fillMaxWidth()
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
                                    text = "$${String.format(Locale.US, "%.2f", ruta.tarifa)}",
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
                            val capacidadMostrar = if (ruta.maxCapacidadBuses > 0) ruta.maxCapacidadBuses else 40
                            InfoCard(
                                icon = Icons.Default.DirectionsBus,
                                label = "Flota Total",
                                value = "${ruta.totalBuses} ${if (ruta.totalBuses == 1) "Unidad" else "Unidades"}",
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                icon = Icons.Default.People,
                                label = "Capacidad",
                                value = "$capacidadMostrar Pasaj.",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Route, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Recorrido", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(10.dp)) {}
                                        Box(modifier = Modifier.width(2.dp).height(24.dp).background(MaterialTheme.colorScheme.outlineVariant))
                                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(10.dp)) {}
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(ruta.origin, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        Spacer(Modifier.height(12.dp))
                                        Text(ruta.destination, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        if (viajesActivos.isNotEmpty()) {
                            Text(
                                text = if (viajesActivos.size == 1) "Unidad en Recorrido" else "Unidades en Recorrido",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))

                            viajesActivos.forEach { viaje ->
                                val capacidadMax = if (ruta.maxCapacidadBuses > 0) ruta.maxCapacidadBuses else 40
                                val ocupados = viaje.numPasajerosTotal
                                val disponibles = (capacidadMax - ocupados).coerceAtLeast(0)

                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = viaje.busPlaca,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold
                                            )

                                            LinearProgressIndicator(
                                                progress = { ocupados.toFloat() / capacidadMax.toFloat() },
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                                color = if (disponibles < 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                            )

                                            Text(
                                                text = "$disponibles ${if (disponibles == 1) "asiento disponible" else "asientos disponibles"}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (disponibles < 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            val qty = cantidadesSeleccionadas[viaje.id] ?: 0
                                            
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                                                    .padding(horizontal = 4.dp)
                                            ) {
                                                IconButton(
                                                    onClick = { if (qty > 0) cantidadesSeleccionadas[viaje.id] = qty - 1 },
                                                    modifier = Modifier.size(32.dp),
                                                    enabled = !isBuying
                                                ) {
                                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }
                                                
                                                Text(
                                                    text = qty.toString(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                )
                                                
                                                IconButton(
                                                    onClick = { if (qty < disponibles) cantidadesSeleccionadas[viaje.id] = qty + 1 },
                                                    modifier = Modifier.size(32.dp),
                                                    enabled = !isBuying && qty < disponibles
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }

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
                                scope.launch {
                                    var comprasTotales = 0
                                    val copiaSeleccion = cantidadesSeleccionadas.toMap()
                                    for ((viajeId, qty) in copiaSeleccion) {
                                        if (qty > 0) {
                                            val comprados = viewModel.comprarPasaje(viajeId, qty)
                                            if (comprados > 0) {
                                                comprasTotales += comprados
                                                cantidadesSeleccionadas[viajeId] = 0
                                            }
                                        }
                                    }
                                    despachoViewModel.addRutaALaCola(ruta, unidadesADespachar, comprasTotales)
                                    asignado = true
                                    val mensaje = if (comprasTotales > 0) 
                                        "Línea asignada y $comprasTotales pasajes adquiridos"
                                    else "Línea asignada a la consola"
                                    snackbarHostState.showSnackbar(mensaje)
                                }
                            },
                            enabled = !asignado && !isBuying,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (asignado) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            if (isBuying) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    if (asignado) Icons.Default.CheckCircle else Icons.Default.ConfirmationNumber, 
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    if (asignado) "Línea en Cola de Monitoreo" else "Comprar y Asignar",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    )
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