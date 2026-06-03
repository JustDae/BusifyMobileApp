package com.daelabs.busify.presentation.viewmodel.admin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.User
import com.daelabs.busify.domain.model.UserPayload
import com.daelabs.busify.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUsuariosState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalCount: Int = 0,
    val search: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class AdminUsuariosViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminUsuariosState())
    val state: State<AdminUsuariosState> = _state

    private var searchJob: Job? = null

    init {
        getUsers()
    }

    fun getUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.getUsers(search = _state.value.search)
                .onSuccess { result ->
                    _state.value = _state.value.copy(
                        users = result.first,
                        totalCount = result.second,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message ?: "Error al cargar usuarios",
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
            getUsers()
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            repository.deleteUser(id)
                .onSuccess {
                    getUsers()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message ?: "Error al eliminar usuario")
                }
        }
    }

    fun toggleActive(id: Int) {
        viewModelScope.launch {
            repository.toggleActive(id)
                .onSuccess {
                    getUsers()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error.message ?: "Error al cambiar estado")
                }
        }
    }

    fun saveUser(id: Int?, payload: UserPayload) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null, saveSuccess = false)
            val result = if (id == null) {
                repository.createUser(payload)
            } else {
                repository.updateUser(id, payload)
            }

            result.onSuccess {
                _state.value = _state.value.copy(isSaving = false, saveSuccess = true)
                getUsers()
            }
            .onFailure { error ->
                _state.value = _state.value.copy(
                    error = error.message ?: "Error al guardar usuario",
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