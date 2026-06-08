package com.daelabs.busify.presentation.viewmodel.admin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusFilters
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaFilters
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.repository.BusRepository
import com.daelabs.busify.domain.repository.RutaRepository
import com.daelabs.busify.domain.repository.ViajeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminViajesState(
    val viajes: List<Viaje> = emptyList(),
    val buses: List<Bus> = emptyList(),
    val rutas: List<Ruta> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalCount: Int = 0,
    val currentPage: Int = 1,
    val statusFilter: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class AdminViajesViewModel @Inject constructor(
    private val repository: ViajeRepository,
    private val busRepository: BusRepository,
    private val rutaRepository: RutaRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminViajesState())
    val state: State<AdminViajesState> = _state

    init {
        getViajes()
        loadBusesAndRutas()
    }

    private fun loadBusesAndRutas() {
        viewModelScope.launch {
            busRepository.getBuses(BusFilters(pageSize = 100)).onSuccess { result ->
                _state.value = _state.value.copy(buses = result.first)
            }
            rutaRepository.getRutas(RutaFilters(pageSize = 100)).onSuccess { result ->
                _state.value = _state.value.copy(rutas = result.first)
            }
        }
    }

    fun getViajes(page: Int = 1, status: String? = _state.value.statusFilter) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, statusFilter = status, currentPage = page)
            repository.getViajes(page, status)
                .onSuccess { result ->
                    _state.value = _state.value.copy(
                        viajes = result.first,
                        totalCount = result.second,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al cargar viajes",
                        isLoading = false
                    )
                }
        }
    }

    fun onStatusFilterChange(status: String?) {
        getViajes(1, status)
    }

    fun updateViajeStatus(id: Int, status: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            repository.updateStatus(id, status)
                .onSuccess {
                    _state.value = _state.value.copy(isSaving = false)
                    getViajes(_state.value.currentPage)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al actualizar estado",
                        isSaving = false
                    )
                }
        }
    }

    fun createViaje(busId: Int, rutaId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null, saveSuccess = false)
            repository.createViaje(busId, rutaId)
                .onSuccess {
                    _state.value = _state.value.copy(isSaving = false, saveSuccess = true)
                    getViajes(1)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al crear viaje",
                        isSaving = false
                    )
                }
        }
    }

    fun updateViaje(id: Int, busId: Int, rutaId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null, saveSuccess = false)
            repository.updateViaje(id, busId, rutaId)
                .onSuccess {
                    _state.value = _state.value.copy(isSaving = false, saveSuccess = true)
                    getViajes(_state.value.currentPage)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al actualizar viaje",
                        isSaving = false
                    )
                }
        }
    }

    fun deleteViaje(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            repository.deleteViaje(id)
                .onSuccess {
                    _state.value = _state.value.copy(isSaving = false)
                    getViajes(_state.value.currentPage)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al eliminar viaje",
                        isSaving = false
                    )
                }
        }
    }

    fun resetSaveStatus() {
        _state.value = _state.value.copy(saveSuccess = false, error = null)
    }

    fun setError(message: String?) {
        _state.value = _state.value.copy(error = message)
    }
}