package com.daelabs.busify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.repository.DespachoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistorialUiState(
    val viajes: List<Viaje> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val total: Int = 0,
    val hasMore: Boolean = false,
    val statusFilter: String = "",
    val page: Int = 1,
)

@HiltViewModel
class HistorialDespachoViewModel @Inject constructor(
    private val repository: DespachoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HistorialUiState())
    val state: StateFlow<HistorialUiState> = _state.asStateFlow()

    init { load() }

    fun load(reset: Boolean = true) {
        val current = _state.value
        val page = if (reset) 1 else current.page

        if (reset) {
            _state.update { it.copy(isLoading = true, error = null, page = 1) }
        } else {
            if (current.isLoadingMore || !current.hasMore) return
            _state.update { it.copy(isLoadingMore = true) }
        }

        viewModelScope.launch {
            repository.getViajesActivos(page = page).onSuccess { (viajes, total) ->
                _state.update { s ->
                    val listaFiltrada = if (current.statusFilter.isBlank()) viajes
                    else viajes.filter { it.status.value == current.statusFilter }
                    s.copy(
                        viajes = if (reset) listaFiltrada else s.viajes + listaFiltrada,
                        total = total,
                        hasMore = (if (reset) listaFiltrada else s.viajes + listaFiltrada).size < total,
                        isLoading = false,
                        isLoadingMore = false,
                        page = page + 1,
                        error = null,
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, isLoadingMore = false, error = e.message) }
            }
        }
    }

    fun setStatusFilter(status: String) {
        _state.update { it.copy(statusFilter = status) }
        load(reset = true)
    }

    fun refresh() = load(reset = true)
    fun loadMore() = load(reset = false)
}