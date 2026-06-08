package com.daelabs.busify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusFilters
import com.daelabs.busify.domain.model.RutaFilters
import com.daelabs.busify.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardStats(
    val totalActiveBuses: Int = 0,
    val inactiveBuses: Int = 0,
    val totalBuses: Int = 0,
    val avgSpeed: Double = 0.0,
    val activeRutas: Int = 0,
    val totalRutas: Int = 0,
    val totalViajes: Int = 0,
    val viajesByStatus: Map<String, Int> = emptyMap(),
    val activeUsers: Int = 0,
    val totalUsers: Int = 0,
    val staffUsers: Int = 0,
    val totalChoferes: Int = 0,
    val availableChoferes: Int = 0,
    val alertBuses: List<Bus> = emptyList(),
    val sectionErrors: Map<String, String> = emptyMap()
)

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val stats: DashboardStats) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val busRepository: BusRepository,
    private val rutaRepository: RutaRepository,
    private val viajeRepository: ViajeRepository,
    private val userRepository: UserRepository,
    private val choferRepository: ChoferRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _lastUpdated = MutableStateFlow<Long>(0L)
    val lastUpdated: StateFlow<Long> = _lastUpdated.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            if (_state.value !is DashboardUiState.Success) {
                _state.value = DashboardUiState.Loading
            }
            _isRefreshing.value = true

            val errors = mutableMapOf<String, String>()

            try {
                val busStatsDef = async { busRepository.getStats() }
                val rutaStatsDef = async { rutaRepository.getStats() }
                val viajeStatsDef = async { viajeRepository.getStats() }
                val userStatsDef = async { userRepository.getStats() }
                val choferStatsDef = async { choferRepository.getStats() }
                val alertDef = async { busRepository.getBuses(BusFilters(estaActivo = false, page = 1, pageSize = 5)) }

                val busRes = busStatsDef.await()
                val rutaRes = rutaStatsDef.await()
                val viajeRes = viajeStatsDef.await()
                val userRes = userStatsDef.await()
                val choferRes = choferStatsDef.await()
                val alertRes = alertDef.await()

                var busData = busRes.getOrNull() ?: emptyMap()
                if (busRes.isFailure) {
                    busRepository.getBuses(BusFilters(page = 1, pageSize = 100)).fold(
                        onSuccess = { (buses, _) ->
                            busData = mapOf(
                                "total_active" to buses.count { it.estaActivo },
                                "total_inactive" to buses.count { !it.estaActivo },
                                "avg_speed" to if (buses.isNotEmpty()) buses.map { it.velocidadKmh }.average() else 0.0
                            )
                        },
                        onFailure = { errors["buses"] = it.message ?: "Error de conexión" }
                    )
                }

                var choferData = choferRes.getOrNull() ?: emptyMap()
                if (choferRes.isFailure) {
                    choferRepository.getChoferes().fold(
                        onSuccess = { choferes ->
                            choferData = mapOf(
                                "total" to choferes.size,
                                "available" to choferes.count { it.isActive }
                            )
                        },
                        onFailure = { errors["choferes"] = it.message ?: "Error de conexión" }
                    )
                }

                var rutaData = rutaRes.getOrNull() ?: emptyMap()
                if (rutaRes.isFailure) {
                    rutaRepository.getRutas(RutaFilters(page = 1, pageSize = 100)).fold(
                        onSuccess = { (rutas, _) ->
                            rutaData = mapOf(
                                "total_active" to rutas.count { it.isActive },
                                "total_inactive" to rutas.count { !it.isActive }
                            )
                        },
                        onFailure = { errors["rutas"] = it.message ?: "Error de conexión" }
                    )
                }

                var viajeData = viajeRes.getOrNull() ?: emptyMap()
                if (viajeRes.isFailure) {
                    viajeRepository.getViajes(page = 1).fold(
                        onSuccess = { (viajes, total) ->
                            viajeData = mapOf(
                                "total_viajes" to total,
                                "by_status" to viajes.groupBy { it.status.value }.mapValues { it.value.size }
                            )
                        },
                        onFailure = { errors["viajes"] = it.message ?: "Error de conexión" }
                    )
                }

                var userData = userRes.getOrNull()?.mapValues { it.value.toAny() } ?: emptyMap()
                if (userRes.isFailure) {
                    userRepository.getUsers(page = 1).fold(
                        onSuccess = { (users, total) ->
                            userData = mapOf(
                                "total" to total,
                                "active" to users.count { it.isActive },
                                "staff" to users.count { it.isStaff }
                            )
                        },
                        onFailure = { errors["usuarios"] = it.message ?: "Error de conexión" }
                    )
                }

                val alertBuses = alertRes.getOrNull()?.first ?: emptyList()

                @Suppress("UNCHECKED_CAST")
                val vStatus = (viajeData["by_status"] as? Map<String, Int>) ?: emptyMap()

                val stats = DashboardStats(
                    totalActiveBuses = (busData["total_active"] as? Int) ?: 0,
                    inactiveBuses = (busData["total_inactive"] as? Int) ?: 0,
                    totalBuses = ((busData["total_active"] as? Int) ?: 0) + ((busData["total_inactive"] as? Int) ?: 0),
                    avgSpeed = (busData["avg_speed"] as? Double) ?: 0.0,
                    activeRutas = (rutaData["total_active"] as? Int) ?: 0,
                    totalRutas = ((rutaData["total_active"] as? Int) ?: 0) + ((rutaData["total_inactive"] as? Int) ?: 0),
                    totalViajes = (viajeData["total_viajes"] as? Int) ?: 0,
                    viajesByStatus = vStatus,
                    activeUsers = (userData["active"] as? Int) ?: 0,
                    totalUsers = (userData["total"] as? Int) ?: 0,
                    staffUsers = (userData["staff"] as? Int) ?: 0,
                    totalChoferes = (choferData["total"] as? Int) ?: 0,
                    availableChoferes = (choferData["available"] as? Int) ?: 0,
                    alertBuses = alertBuses,
                    sectionErrors = errors
                )

                _state.value = DashboardUiState.Success(stats)
                _lastUpdated.value = System.currentTimeMillis()

            } catch (e: Exception) {
                if (_state.value !is DashboardUiState.Success) {
                    _state.value = DashboardUiState.Error(e.message ?: "Error al cargar el dashboard")
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

private fun Int.toAny(): Any = this
