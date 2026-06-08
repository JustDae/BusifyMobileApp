package com.daelabs.busify.presentation.ui.admin.rutas

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
import com.daelabs.busify.presentation.viewmodel.admin.AdminRutasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRutaFormScreen(
    rutaId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: AdminRutasViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var tarifa by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var cooperativaId by remember { mutableStateOf<Int?>(null) }
    var expandedCooperativa by remember { mutableStateOf(false) }

    LaunchedEffect(rutaId, state.rutas) {
        if (rutaId != null && state.rutas.isNotEmpty()) {
            val ruta = state.rutas.find { it.id == rutaId }
            ruta?.let {
                name = it.name
                description = it.description
                origin = it.origin
                destination = it.destination
                tarifa = it.tarifa.toString()
                isActive = it.isActive
                
                if (cooperativaId == null) {
                    cooperativaId = it.cooperativaId
                }
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
                title = { Text(if (rutaId == null) "Nueva Ruta" else "Editar Ruta") },
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la Ruta") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("Origen") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tarifa,
                onValueChange = { tarifa = it },
                label = { Text("Base Fare") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            ExposedDropdownMenuBox(
                expanded = expandedCooperativa,
                onExpandedChange = { expandedCooperativa = !expandedCooperativa },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.cooperativas.find { it.id == cooperativaId }?.name ?: "Seleccione Cooperativa",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cooperativa") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCooperativa) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedCooperativa,
                    onDismissRequest = { expandedCooperativa = false }
                ) {
                    state.cooperativas.forEach { cooperativa ->
                        DropdownMenuItem(
                            text = { Text(cooperativa.name) },
                            onClick = {
                                cooperativaId = cooperativa.id
                                expandedCooperativa = false
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
                    checked = isActive,
                    onCheckedChange = { isActive = it }
                )
            }

            if (state.error != null) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val tarifaDouble = tarifa.toDoubleOrNull()
                    if (name.isBlank()) {
                        viewModel.setError("El nombre es obligatorio")
                    } else if (origin.isBlank()) {
                        viewModel.setError("El origen es obligatorio")
                    } else if (destination.isBlank()) {
                        viewModel.setError("El destino es obligatorio")
                    } else if (tarifaDouble == null || tarifaDouble <= 0) {
                        viewModel.setError("Ingrese una tarifa válida mayor a 0")
                    } else {
                        viewModel.saveRuta(rutaId, name, description, origin, destination, tarifaDouble, isActive, cooperativaId)
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