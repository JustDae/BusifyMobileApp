package com.daelabs.busify.presentation.ui.admin.buses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daelabs.busify.domain.model.BusPayload
import com.daelabs.busify.presentation.viewmodel.admin.AdminBusesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBusFormScreen(
    busId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: AdminBusesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var numeroBus by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var rutaActual by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") }
    var estaActivo by remember { mutableStateOf(true) }
    var choferId by remember { mutableStateOf("") }

    LaunchedEffect(busId) {
        if (busId != null) {
            val bus = state.buses.find { it.id == busId }
            bus?.let {
                numeroBus = it.numeroBus.toString()
                placa = it.placa
                rutaActual = it.rutaActual
                capacidad = it.capacidadPasajeros.toString()
                estaActivo = it.estaActivo
                choferId = it.choferId?.toString() ?: ""
            }
        }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            viewModel.resetSaveStatus()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (busId == null) "Nuevo Bus" else "Editar Bus") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = numeroBus,
                onValueChange = { numeroBus = it },
                label = { Text("Número de Bus") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = placa,
                onValueChange = { placa = it },
                label = { Text("Placa") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rutaActual,
                onValueChange = { rutaActual = it },
                label = { Text("Ruta Actual (Nombre)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = capacidad,
                onValueChange = { capacidad = it },
                label = { Text("Capacidad de Pasajeros") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = choferId,
                onValueChange = { choferId = it },
                label = { Text("ID del Chofer") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("¿Está activo?")
                Switch(
                    checked = estaActivo,
                    onCheckedChange = { estaActivo = it }
                )
            }

            if (state.error != null) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (numeroBus.isBlank()) {
                        viewModel.setError("El número de bus es obligatorio")
                    } else if (placa.isBlank()) {
                        viewModel.setError("La placa es obligatoria")
                    } else if (capacidad.toIntOrNull() == null || capacidad.toIntOrNull()!! <= 0) {
                        viewModel.setError("Ingrese una capacidad válida")
                    } else {
                        val payload = BusPayload(
                            numeroBus = numeroBus.toIntOrNull() ?: 0,
                            placa = placa,
                            rutaActual = rutaActual,
                            capacidadPasajeros = capacidad.toIntOrNull() ?: 0,
                            estaActivo = estaActivo,
                            choferId = choferId.toIntOrNull() ?: 0
                        )
                        viewModel.saveBus(busId, payload)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Guardar", color = Color.White)
                }
            }
        }
    }
}