package com.daelabs.busify.presentation.viewmodel.admin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusFilters
import com.daelabs.busify.domain.model.BusPayload
import com.daelabs.busify.domain.model.Chofer
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaFilters
import com.daelabs.busify.domain.repository.BusRepository
import com.daelabs.busify.domain.repository.ChoferRepository
import com.daelabs.busify.domain.repository.RutaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminBusesState(
    val buses: List<Bus> = emptyList(),
    val choferes: List<Chofer> = emptyList(),
    val rutas: List<Ruta> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filters: BusFilters = BusFilters(),
    val totalCount: Int = 0,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class AdminBusesViewModel @Inject constructor(
    private val repository: BusRepository,
    private val choferRepository: ChoferRepository,
    private val rutaRepository: RutaRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminBusesState())
    val state: State<AdminBusesState> = _state

    private var searchJob: Job? = null

    init {
        getBuses()
        getChoferes()
        getRutas()
    }

    fun getRutas() {
        viewModelScope.launch {
            rutaRepository.getRutas(RutaFilters(pageSize = 100)).onSuccess { result ->
                _state.value = _state.value.copy(rutas = result.first)
            }.onFailure { error ->
                _state.value = _state.value.copy(error = error.message ?: "Error al cargar rutas")
            }
        }
    }

    fun getChoferes() {
        viewModelScope.launch {
            choferRepository.getChoferes().onSuccess { choferes ->
                _state.value = _state.value.copy(choferes = choferes)
            }.onFailure { error ->
                _state.value = _state.value.copy(error = error.message ?: "Error al cargar choferes")
            }
        }
    }

    fun getBuses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.getBuses(_state.value.filters)
                .onSuccess { result ->
                    _state.value = _state.value.copy(
                        buses = result.first,
                        totalCount = result.second,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al cargar buses",
                        isLoading = false
                    )
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            filters = _state.value.filters.copy(search = query, page = 1)
        )
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            getBuses()
        }
    }

    fun deleteBus(id: Int) {
        viewModelScope.launch {
            repository.deleteBus(id)
                .onSuccess {
                    getBuses()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message ?: "Error al eliminar bus")
                }
        }
    }

    fun saveBus(id: Int?, payload: BusPayload) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null, saveSuccess = false)
            val result = if (id == null) {
                repository.createBus(payload)
            } else {
                repository.updateBus(id, payload)
            }

            result.onSuccess {
                _state.value = _state.value.copy(isSaving = false, saveSuccess = true)
                getBuses()
            }
            .onFailure { error ->
                _state.value = _state.value.copy(
                    error = error.message ?: "Error al guardar bus",
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