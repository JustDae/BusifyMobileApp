package com.daelabs.busify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.repository.DespachoRepository
import com.daelabs.busify.domain.repository.ViajeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ViajeDetailUiState {
    data object Loading : ViajeDetailUiState
    data class Success(val viaje: Viaje) : ViajeDetailUiState
    data class Error(val message: String) : ViajeDetailUiState
}

data class MonitoreoItem(
    val ruta: Ruta,
    val busesAsignados: Int,
)

@HiltViewModel
class MonitoreoViewModel @Inject constructor(
    private val repository: DespachoRepository,
    private val viajeRepository: ViajeRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<MonitoreoItem>>(emptyList())
    val items: StateFlow<List<MonitoreoItem>> = _items.asStateFlow()

    private val _detailState = MutableStateFlow<ViajeDetailUiState>(ViajeDetailUiState.Loading)
    val detailState: StateFlow<ViajeDetailUiState> = _detailState.asStateFlow()

    val totalBusesMonitoreados: StateFlow<Int> = _items
        .map { list -> list.sumOf { it.busesAsignados } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalRutasSeguidas: StateFlow<Int> = _items
        .map { list -> list.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun loadViajeDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = ViajeDetailUiState.Loading
            viajeRepository.getViaje(id)
                .onSuccess { viaje ->
                    _detailState.value = ViajeDetailUiState.Success(viaje)
                }
                .onFailure { error ->
                    _detailState.value = ViajeDetailUiState.Error(error.message ?: "Fallo de conexión remota")
                }
        }
    }

    fun monitorearRuta(ruta: Ruta, buses: Int = 1) {
        _items.update { list ->
            val existing = list.find { it.ruta.id == ruta.id }
            if (existing != null) {
                list.map {
                    if (it.ruta.id == ruta.id)
                        it.copy(busesAsignados = it.busesAsignados + buses)
                    else it
                }
            } else {
                list + MonitoreoItem(ruta, buses)
            }
        }
    }

    fun updateBuses(rutaId: Int, buses: Int) {
        if (buses <= 0) removerRuta(rutaId)
        else _items.update { list ->
            list.map { if (it.ruta.id == rutaId) it.copy(busesAsignados = buses) else it }
        }
    }

    fun removerRuta(rutaId: Int) {
        _items.update { list -> list.filter { it.ruta.id != rutaId } }
    }

    fun clearMonitoreo() { _items.value = emptyList() }
}