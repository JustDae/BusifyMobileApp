package com.daelabs.busify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Cooperativa
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaFilters
import com.daelabs.busify.domain.repository.CooperativaRepository
import com.daelabs.busify.domain.repository.RutaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CatalogUiState(
    val rutas: List<Ruta> = emptyList(),
    val cooperativas: List<Cooperativa> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val total: Int = 0,
    val hasMore: Boolean = false,
    val search: String = "",
    val selectedCooperativa: Int? = null,
    val ordering: String = "",
    val page: Int = 1,
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val rutaRepository: RutaRepository,
    private val cooperativaRepository: CooperativaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init { loadCooperativas(); load() }

    private fun loadCooperativas() {
        viewModelScope.launch {
            cooperativaRepository.getCooperativas().onSuccess { coops ->
                _state.update { it.copy(cooperativas = coops.filter { c -> c.isActive }) }
            }
        }
    }

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
            val filters = RutaFilters(
                search = current.search.ifBlank { null },
                cooperativa = current.selectedCooperativa,
                ordering = current.ordering.ifBlank { null },
                isActive = true,
                page = page,
                pageSize = 12,
            )
            rutaRepository.getRutas(filters)
                .onSuccess { (rutas, total) ->
                    _state.update { s ->
                        s.copy(
                            rutas = if (reset) rutas else s.rutas + rutas,
                            total = total,
                            hasMore = (if (reset) rutas else s.rutas + rutas).size < total,
                            isLoading = false,
                            isLoadingMore = false,
                            page = page + 1,
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, isLoadingMore = false, error = e.message) }
                }
        }
    }

    fun setSearch(query: String) {
        _state.update { it.copy(search = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            load(reset = true)
        }
    }

    fun setCooperativa(id: Int?) {
        _state.update { it.copy(selectedCooperativa = id) }
        load(reset = true)
    }

    fun setOrdering(ordering: String) {
        _state.update { it.copy(ordering = ordering) }
        load(reset = true)
    }

    fun loadMore() = load(reset = false)
    fun refresh() = load(reset = true)
}