package com.daelabs.busify.presentation.viewmodel.admin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Chofer
import com.daelabs.busify.domain.model.ChoferPayload
import com.daelabs.busify.domain.repository.ChoferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminChoferesState(
    val choferes: List<Chofer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val search: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class AdminChoferesViewModel @Inject constructor(
    private val repository: ChoferRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminChoferesState())
    val state: State<AdminChoferesState> = _state

    private var searchJob: Job? = null

    init {
        getChoferes()
    }

    fun getChoferes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.getChoferes(_state.value.search)
                .onSuccess { list ->
                    _state.value = _state.value.copy(
                        choferes = list,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al cargar choferes",
                        isLoading = false
                    )
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(search = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            getChoferes()
        }
    }

    fun deleteChofer(id: Int) {
        viewModelScope.launch {
            repository.deleteChofer(id)
                .onSuccess {
                    getChoferes()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message ?: "Error al eliminar chofer")
                }
        }
    }

    fun saveChofer(id: Int?, payload: ChoferPayload) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null, saveSuccess = false)
            val result = if (id == null) {
                repository.createChofer(payload)
            } else {
                repository.updateChofer(id, payload)
            }

            result.onSuccess {
                _state.value = _state.value.copy(isSaving = false, saveSuccess = true)
                getChoferes()
            }
            .onFailure { error ->
                _state.value = _state.value.copy(
                    error = error.message ?: "Error al guardar chofer",
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