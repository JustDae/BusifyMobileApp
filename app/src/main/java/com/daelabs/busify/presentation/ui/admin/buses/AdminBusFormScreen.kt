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
    var modelo by remember { mutableStateOf("") }
    var rutaId by remember { mutableStateOf<Int?>(null) }
    var capacidad by remember { mutableStateOf("") }
    var estaActivo by remember { mutableStateOf(true) }
    var choferId by remember { mutableStateOf("") }
    var choferExpanded by remember { mutableStateOf(false) }
    var rutaExpanded by remember { mutableStateOf(false) }

    val selectedChofer = state.choferes.find { it.id.toString() == choferId }
    val choferDisplayText = selectedChofer?.fullName ?: "Seleccionar Chofer"

    val selectedRuta = state.rutas.find { it.id == rutaId }
    val rutaDisplayText = selectedRuta?.name ?: "Seleccionar Ruta"

    LaunchedEffect(busId, state.buses) {
        if (busId != null && state.buses.isNotEmpty()) {
            val bus = state.buses.find { it.id == busId }
            bus?.let {
                numeroBus = it.numeroBus
                placa = it.placa
                modelo = ""
                rutaId = it.rutaId
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
                value = modelo,
                onValueChange = { modelo = it },
                label = { Text("Modelo (Ej: Mercedes Benz 2023)") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = rutaExpanded,
                onExpandedChange = { rutaExpanded = !rutaExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = rutaDisplayText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Asignar Ruta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rutaExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = rutaExpanded,
                    onDismissRequest = { rutaExpanded = false }
                ) {
                    state.rutas.forEach { ruta ->
                        DropdownMenuItem(
                            text = { Text(ruta.name) },
                            onClick = {
                                rutaId = ruta.id
                                rutaExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = capacidad,
                onValueChange = { capacidad = it },
                label = { Text("Capacidad de Pasajeros") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            ExposedDropdownMenuBox(
                expanded = choferExpanded,
                onExpandedChange = { choferExpanded = !choferExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = choferDisplayText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Asignar Chofer") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = choferExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = choferExpanded,
                    onDismissRequest = { choferExpanded = false }
                ) {
                    state.choferes.forEach { chofer ->
                        DropdownMenuItem(
                            text = { Text("${chofer.fullName} (${chofer.licenseNumber})") },
                            onClick = {
                                choferId = chofer.id.toString()
                                choferExpanded = false
                            }
                        )
                    }
                }
            }

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
                    } else if (modelo.isBlank()) {
                        viewModel.setError("El modelo es obligatorio")
                    } else if (capacidad.toIntOrNull() == null || capacidad.toIntOrNull()!! <= 0) {
                        viewModel.setError("Ingrese una capacidad válida")
                    } else {
                        val payload = BusPayload(
                            numeroBus = numeroBus,
                            placa = placa,
                            modelo = modelo,
                            rutaId = rutaId,
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