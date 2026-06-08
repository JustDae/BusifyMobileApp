package com.daelabs.busify.presentation.ui.uipublic.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.daelabs.busify.presentation.viewmodel.DespachoViewModel
import com.daelabs.busify.presentation.viewmodel.DespachoFlujoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DespachoBottomSheet(
    viewModel: DespachoViewModel,
    isOperadorAutenticado: Boolean,
    onDismiss: () -> Unit,
    onAuthRequired: () -> Unit,
    onDespachoConfirmado: (Int) -> Unit,
) {
    val cola by viewModel.colaDespacho.collectAsState()
    val costoTotal by viewModel.costoOperacionEstimado.collectAsState()
    val flujoState by viewModel.flujoState.collectAsState()

    LaunchedEffect(flujoState) {
        if (flujoState is DespachoFlujoState.Exito) {
            onDespachoConfirmado((flujoState as DespachoFlujoState.Exito).viajeId)
            viewModel.resetFlujo()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(modifier = Modifier.padding(vertical = 12.dp).size(40.dp, 4.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(2.dp)))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Consola de Despacho", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${cola.size} líneas en espera de asignación", style = MaterialTheme.typography.bodySmall)
                }
                if (cola.isNotEmpty()) {
                    IconButton(onClick = viewModel::vaciarCola) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Limpiar consola", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp)

            if (cola.isEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🚌", fontSize = 52.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Consola vacía", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Asigne unidades desde las vistas de detalle.", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cola, key = { it.ruta.id }) { item ->
                        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.ruta.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (item.unidadesSolicitadas > 0) {
                                        Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                text = "${item.unidadesSolicitadas} unidades a despachar",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    if (item.unidadesSolicitadas > 0 && item.pasajesComprados > 0) {
                                        Text(" + ", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp))
                                    }
                                    if (item.pasajesComprados > 0) {
                                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                text = "${item.pasajesComprados} pasajes adquiridos",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }
                                }
                            }
                            IconButton(onClick = { viewModel.removerDeLaCola(item.ruta.id) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 0.5.dp)

                Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal Operativo", style = MaterialTheme.typography.bodyMedium)
                    Text("$${String.format(Locale.US, "%.2f", costoTotal)}", fontWeight = FontWeight.Bold)
                }

                if (flujoState is DespachoFlujoState.Error) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = (flujoState as DespachoFlujoState.Error).error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                val estaCargando = flujoState is DespachoFlujoState.Procesando
                Button(
                    onClick = { if (!isOperadorAutenticado) onAuthRequired() else viewModel.ejecutarDespachoMasivo() },
                    enabled = !estaCargando,
                    modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 24.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (estaCargando) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Confirmar Despacho y Poner En Ruta")
                    }
                }
            }
        }
    }
}