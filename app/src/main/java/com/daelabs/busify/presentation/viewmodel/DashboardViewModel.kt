package com.daelabs.busify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusFilters
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

    private val _lastUpdated = MutableStateFlow<Long>(0L)
    val lastUpdated: StateFlow<Long> = _lastUpdated.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = DashboardUiState.Loading

            try {
                val busStatsDeferred = async { busRepository.getStats() }
                val rutaStatsDeferred = async { rutaRepository.getStats() }
                val viajeStatsDeferred = async { viajeRepository.getStats() }
                val userStatsDeferred = async { userRepository.getStats() }
                val choferStatsDeferred = async { choferRepository.getStats() }
                val alertBusesDeferred = async {
                    busRepository.getBuses(
                        BusFilters(estaActivo = false, page = 1, pageSize = 5)
                    )
                }

                val busStats = busStatsDeferred.await().getOrDefault(emptyMap())
                val rutaStats = rutaStatsDeferred.await().getOrDefault(emptyMap())
                val viajeStats = viajeStatsDeferred.await().getOrDefault(emptyMap())
                val userStats = userStatsDeferred.await().getOrDefault(emptyMap())
                val choferStats = choferStatsDeferred.await().getOrDefault(emptyMap())
                val alertBuses = alertBusesDeferred.await().getOrNull()

                @Suppress("UNCHECKED_CAST")
                val viajesByStatus = (viajeStats["by_status"] as? Map<String, Int>) ?: emptyMap()

                val stats = DashboardStats(
                    totalActiveBuses = (busStats["total_active"] as? Int) ?: 0,
                    inactiveBuses = (busStats["total_inactive"] as? Int) ?: 0,
                    totalBuses = ((busStats["total_active"] as? Int) ?: 0) + ((busStats["total_inactive"] as? Int) ?: 0),
                    avgSpeed = (busStats["avg_speed"] as? Double) ?: 0.0,
                    activeRutas = (rutaStats["total_active"] as? Int) ?: 0,
                    totalRutas = ((rutaStats["total_active"] as? Int) ?: 0) + ((rutaStats["total_inactive"] as? Int) ?: 0),
                    totalViajes = (viajeStats["total_viajes"] as? Int) ?: 0,
                    viajesByStatus = viajesByStatus,
                    activeUsers = (userStats["active"] as? Int) ?: 0,
                    totalUsers = (userStats["total"] as? Int) ?: 0,
                    staffUsers = (userStats["staff"] as? Int) ?: 0,
                    totalChoferes = (choferStats["total"] as? Int) ?: 0,
                    availableChoferes = (choferStats["available"] as? Int) ?: 0,
                    alertBuses = alertBuses?.first ?: emptyList()
                )

                _state.value = DashboardUiState.Success(stats)
                _lastUpdated.value = System.currentTimeMillis()

            } catch (e: Exception) {
                _state.value = DashboardUiState.Error(e.message ?: "Error al cargar el dashboard")
            }
        }
    }
}