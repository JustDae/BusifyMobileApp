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

    init { 
        loadCooperativas()
        load(reset = true) 
    }

    private fun loadCooperativas() {
        viewModelScope.launch {
            cooperativaRepository.getCooperativas().onSuccess { coops ->
                _state.update { it.copy(cooperativas = coops.filter { c -> c.isActive }) }
            }
        }
    }

    fun load(reset: Boolean = true) {
        if (reset) {
            _state.update { it.copy(isLoading = true, error = null, page = 1, rutas = emptyList()) }
        } else {
            if (_state.value.isLoadingMore || !_state.value.hasMore) return
            _state.update { it.copy(isLoadingMore = true) }
        }

        val snapshot = _state.value

        viewModelScope.launch {
            val filters = RutaFilters(
                search = snapshot.search.ifBlank { null },
                cooperativa = snapshot.selectedCooperativa,
                ordering = snapshot.ordering.ifBlank { null },
                isActive = null,
                page = snapshot.page,
                pageSize = if (snapshot.selectedCooperativa != null || snapshot.search.isNotBlank()) 50 else 12,
            )
            
            rutaRepository.getRutas(filters)
                .onSuccess { (rutasRaw, serverTotal) ->
                    val filtered = rutasRaw.filter { ruta ->
                        val matchesCoop = snapshot.selectedCooperativa == null || 
                                         ruta.cooperativaId == snapshot.selectedCooperativa
                        
                        val matchesSearch = snapshot.search.isBlank() || 
                                          ruta.name.contains(snapshot.search, ignoreCase = true) ||
                                          (ruta.cooperativaName?.contains(snapshot.search, ignoreCase = true) ?: false)
                        
                        matchesCoop && matchesSearch
                    }

                    _state.update { st ->
                        st.copy(
                            rutas = if (reset) filtered else st.rutas + filtered,
                            total = if (snapshot.selectedCooperativa == null && snapshot.search.isBlank()) serverTotal else filtered.size,
                            hasMore = if (snapshot.selectedCooperativa == null && snapshot.search.isBlank()) 
                                        (if (reset) filtered else st.rutas + filtered).size < serverTotal 
                                      else false,
                            isLoading = false,
                            isLoadingMore = false,
                            page = snapshot.page + 1,
                            error = null
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
        val currentId = _state.value.selectedCooperativa
        val nextId = if (currentId == id) null else id
        _state.update { it.copy(selectedCooperativa = nextId) }
        load(reset = true)
    }

    fun setOrdering(ordering: String) {
        _state.update { it.copy(ordering = ordering) }
        load(reset = true)
    }

    fun loadMore() = load(reset = false)
    fun refresh() = load(reset = true)
}
