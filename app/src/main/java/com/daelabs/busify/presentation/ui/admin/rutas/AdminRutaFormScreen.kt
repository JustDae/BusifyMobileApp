package com.daelabs.busify.presentation.ui.admin.rutas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    var tarifa by remember { mutableStateOf("") }
    var cooperativaId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(rutaId) {
        if (rutaId != null) {
            val ruta = state.rutas.find { it.id == rutaId }
            ruta?.let {
                name = it.name
                tarifa = it.tarifa.toString()
                // cooperativaId = it.cooperativaId // Assuming we have this in Ruta model if needed
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
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
                value = tarifa,
                onValueChange = { tarifa = it },
                label = { Text("Tarifa") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            if (state.error != null) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val tarifaDouble = tarifa.toDoubleOrNull()
                    if (name.isBlank()) {
                        viewModel.setError("El nombre es obligatorio")
                    } else if (tarifaDouble == null || tarifaDouble <= 0) {
                        viewModel.setError("Ingrese una tarifa válida mayor a 0")
                    } else {
                        viewModel.saveRuta(rutaId, name, tarifaDouble, cooperativaId)
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