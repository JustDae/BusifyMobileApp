package com.daelabs.busify.presentation.viewmodel.admin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaFilters
import com.daelabs.busify.domain.model.RutaPayload
import com.daelabs.busify.domain.repository.RutaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminRutasState(
    val rutas: List<Ruta> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filters: RutaFilters = RutaFilters(),
    val totalCount: Int = 0,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class AdminRutasViewModel @Inject constructor(
    private val repository: RutaRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminRutasState())
    val state: State<AdminRutasState> = _state

    private var searchJob: Job? = null

    init {
        getRutas()
    }

    fun getRutas() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.getRutas(_state.value.filters)
                .onSuccess { result ->
                    _state.value = _state.value.copy(
                        rutas = result.first,
                        totalCount = result.second,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al cargar rutas",
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
            getRutas()
        }
    }

    fun deleteRuta(id: Int) {
        viewModelScope.launch {
            repository.deleteRuta(id)
                .onSuccess {
                    getRutas()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message ?: "Error al eliminar ruta")
                }
        }
    }

    fun saveRuta(id: Int?, name: String, tarifa: Double, cooperativaId: Int?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null, saveSuccess = false)
            val payload = RutaPayload(name, tarifa, cooperativaId)
            val result = if (id == null) {
                repository.createRuta(payload)
            } else {
                repository.updateRuta(id, payload)
            }

            result.onSuccess {
                _state.value = _state.value.copy(isSaving = false, saveSuccess = true)
                getRutas()
            }
            .onFailure { error ->
                _state.value = _state.value.copy(
                    error = error.message ?: "Error al guardar ruta",
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