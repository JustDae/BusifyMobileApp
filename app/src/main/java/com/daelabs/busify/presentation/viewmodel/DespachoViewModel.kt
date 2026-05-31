package com.daelabs.busify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.repository.DespachoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DespachoItem(
    val ruta: Ruta,
    val unidadesSolicitadas: Int,
)

sealed interface DespachoFlujoState {
    data object Idle : DespachoFlujoState
    data object Procesando : DespachoFlujoState
    data class Exito(val viajeId: Int) : DespachoFlujoState
    data class Error(val error: String) : DespachoFlujoState
}

@HiltViewModel
class DespachoViewModel @Inject constructor(
    private val repository: DespachoRepository,
) : ViewModel() {

    private val _colaDespacho = MutableStateFlow<List<DespachoItem>>(emptyList())
    val colaDespacho: StateFlow<List<DespachoItem>> = _colaDespacho.asStateFlow()

    val totalUnidadesModeladas = _colaDespacho
        .map { it.sumOf { i -> i.unidadesSolicitadas } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val costoOperacionEstimado = _colaDespacho
        .map { it.sumOf { i -> i.ruta.tarifa * i.unidadesSolicitadas } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    private val _flujoState = MutableStateFlow<DespachoFlujoState>(DespachoFlujoState.Idle)
    val flujoState: StateFlow<DespachoFlujoState> = _flujoState.asStateFlow()

    fun addRutaALaCola(ruta: Ruta, unidades: Int) {
        _colaDespacho.update { current ->
            val existing = current.find { it.ruta.id == ruta.id }
            if (existing != null) {
                current.map { if (it.ruta.id == ruta.id) it.copy(unidadesSolicitadas = unidades) else it }
            } else {
                current + DespachoItem(ruta, unidades)
            }
        }
    }

    fun removerDeLaCola(rutaId: Int) {
        _colaDespacho.update { it.filter { i -> i.ruta.id != rutaId } }
    }

    fun vaciarCola() { _colaDespacho.value = emptyList() }
    fun resetFlujo() { _flujoState.value = DespachoFlujoState.Idle }

    fun ejecutarDespachoMasivo() {
        val currentItems = _colaDespacho.value
        if (currentItems.isEmpty()) return

        viewModelScope.launch {
            _flujoState.value = DespachoFlujoState.Procesando

            val itemActual = currentItems.firstOrNull()
            if (itemActual == null) {
                _flujoState.value = DespachoFlujoState.Error("No hay líneas en espera en la consola")
                return@launch
            }

            repository.crearDespacho(
                rutaId = itemActual.ruta.id,
                busesDespachados = itemActual.unidadesSolicitadas
            ).onSuccess { maestro ->

                var asignacionExitosa = true
                for (item in currentItems) {
                    repository.asignarUnidad(maestro.id, item.ruta.id, busId = 1, choferId = 1).getOrElse {
                        asignacionExitosa = false
                        _flujoState.value = DespachoFlujoState.Error("Fallo en asignación local: ${item.ruta.name}")
                    }
                }

                if (!asignacionExitosa) return@launch

                repository.confirmarDespacho(maestro.id).onSuccess { transaccion ->
                    vaciarCola()
                    _flujoState.value = DespachoFlujoState.Exito(transaccion.id)
                }.onFailure {
                    _flujoState.value = DespachoFlujoState.Error("Error al levantar el servicio")
                }

            }.onFailure { error ->
                _flujoState.value = DespachoFlujoState.Error(error.message ?: "Infraestructura backend no responde")
            }
        }
    }
}