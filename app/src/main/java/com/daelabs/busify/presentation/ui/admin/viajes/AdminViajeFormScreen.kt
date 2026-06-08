package com.daelabs.busify.presentation.ui.admin.viajes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daelabs.busify.presentation.viewmodel.admin.AdminViajesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminViajeFormScreen(
    viajeId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: AdminViajesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var busId by remember { mutableStateOf<Int?>(null) }
    var rutaId by remember { mutableStateOf<Int?>(null) }
    
    var busExpanded by remember { mutableStateOf(false) }
    var rutaExpanded by remember { mutableStateOf(false) }

    val selectedBus = state.buses.find { it.id == busId }
    val busDisplayText = selectedBus?.let { "${it.numeroBus} - ${it.placa}" } ?: "Seleccionar Bus"

    val selectedRuta = state.rutas.find { it.id == rutaId }
    val rutaDisplayText = selectedRuta?.name ?: "Seleccionar Ruta"

    LaunchedEffect(viajeId, state.viajes) {
        if (viajeId != null && state.viajes.isNotEmpty()) {
            val viaje = state.viajes.find { it.id == viajeId }
            viaje?.let {
                busId = it.busId
                rutaId = it.rutaId
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
                title = { Text(if (viajeId == null) "Nuevo Viaje" else "Editar Viaje") },
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
            ExposedDropdownMenuBox(
                expanded = busExpanded,
                onExpandedChange = { busExpanded = !busExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = busDisplayText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Asignar Bus") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = busExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = busExpanded,
                    onDismissRequest = { busExpanded = false }
                ) {
                    state.buses.forEach { bus ->
                        DropdownMenuItem(
                            text = { Text("${bus.numeroBus} - ${bus.placa}") },
                            onClick = {
                                busId = bus.id
                                busExpanded = false
                            }
                        )
                    }
                }
            }

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

            if (state.error != null) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (busId == null) {
                        viewModel.setError("Debe seleccionar un bus")
                    } else if (rutaId == null) {
                        viewModel.setError("Debe seleccionar una ruta")
                    } else {
                        if (viajeId == null) {
                            viewModel.createViaje(busId!!, rutaId!!)
                        } else {
                            viewModel.updateViaje(viajeId, busId!!, rutaId!!)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Guardar Viaje", color = Color.White)
                }
            }
        }
    }
}
