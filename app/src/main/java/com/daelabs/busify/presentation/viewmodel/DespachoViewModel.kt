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
    val pasajesComprados: Int = 0
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
        .map { it.sumOf { i -> i.ruta.tarifa * (i.unidadesSolicitadas + i.pasajesComprados) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    private val _flujoState = MutableStateFlow<DespachoFlujoState>(DespachoFlujoState.Idle)
    val flujoState: StateFlow<DespachoFlujoState> = _flujoState.asStateFlow()

    fun addRutaALaCola(ruta: Ruta, unidades: Int, pasajes: Int = 0) {
        _colaDespacho.update { current ->
            val existing = current.find { it.ruta.id == ruta.id }
            if (existing != null) {
                current.map { 
                    if (it.ruta.id == ruta.id) 
                        it.copy(
                            unidadesSolicitadas = unidades, 
                            pasajesComprados = it.pasajesComprados + pasajes 
                        ) 
                    else it 
                }
            } else {
                current + DespachoItem(ruta, unidades, pasajes)
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

            var lastViajeId = -1
            var hasError = false
            var errorMessage = ""

            for (item in currentItems) {
                for (i in 1..item.unidadesSolicitadas) {
                    repository.crearDespacho(
                        rutaId = item.ruta.id,
                        busesDespachados = 1
                    ).onSuccess { maestro ->
                        lastViajeId = maestro.id
                        kotlinx.coroutines.delay(150)
                    }.onFailure { error ->
                        hasError = true
                        errorMessage = "Error despachando unidad ${i} de ${item.ruta.name}: ${error.message}"
                    }
                    if (hasError) break
                }
                if (hasError) break
            }

            if (hasError) {
                _flujoState.value = DespachoFlujoState.Error(errorMessage)
            } else {
                vaciarCola()
                _flujoState.value = DespachoFlujoState.Exito(lastViajeId)
            }
        }
    }
}