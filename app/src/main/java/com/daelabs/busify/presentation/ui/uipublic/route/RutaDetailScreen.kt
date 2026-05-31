package com.daelabs.busify.presentation.ui.uipublic.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val subtotalTarifa = ruta.tarifa * unidadesADespachar

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
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
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) { Text("📍", fontSize = 64.sp) }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f), RoundedCornerShape(50)),
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            ruta.cooperativaName?.let {
                Text(text = it.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
            }

            Text(text = ruta.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Text(text = "$${"%.2f".format(ruta.tarifa)} Tarifa Base", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "Disponibilidad de flota: Alta", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(16.dp))

            HorizontalDivider(thickness = 0.5.dp)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Flota a Despachar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (unidadesADespachar > 1) unidadesADespachar-- }) {
                        Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                    }
                    Text(text = unidadesADespachar.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                    IconButton(onClick = { if (unidadesADespachar < 15) unidadesADespachar++ }) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    despachoViewModel.addRutaALaCola(ruta, unidadesADespachar)
                    asignado = true
                },
                enabled = !asignado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Icon(if (asignado) Icons.Default.Check else Icons.Default.DirectionsBus, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (asignado) "¡Línea en cola de Despacho!" else "Asignar a Consola de Monitoreo")
            }

            LaunchedEffect(asignado) {
                if (asignado) {
                    kotlinx.coroutines.delay(2000)
                    asignado = false
                }
            }
        }
    }
}