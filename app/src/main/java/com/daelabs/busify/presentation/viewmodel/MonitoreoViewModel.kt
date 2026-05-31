package com.daelabs.busify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Ruta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class MonitoreoItem(
    val ruta: Ruta,
    val busesAsignados: Int,
)

@HiltViewModel
class MonitoreoViewModel @Inject constructor() : ViewModel() {

    private val _items = MutableStateFlow<List<MonitoreoItem>>(emptyList())
    val items: StateFlow<List<MonitoreoItem>> = _items.asStateFlow()

    val totalBusesMonitoreados: StateFlow<Int> = _items
        .map { list -> list.sumOf { it.busesAsignados } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalRutasSeguidas: StateFlow<Int> = _items
        .map { list -> list.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun monitorearRuta(ruta: Ruta, buses: Int = 1) {
        _items.update { list ->
            val existing = list.find { it.ruta.id == ruta.id }
            if (existing != null) {
                list.map {
                    if (it.ruta.id == ruta.id)
                        it.copy(busesAsignados = minOf(it.busesAsignados + buses, ruta.maxCapacidadBuses))
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