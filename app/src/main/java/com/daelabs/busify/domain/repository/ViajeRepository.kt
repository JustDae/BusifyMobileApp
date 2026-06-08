package com.daelabs.busify.domain.repository

import com.daelabs.busify.domain.model.Viaje

interface ViajeRepository {
    suspend fun getViajes(page: Int? = null, status: String? = null): Result<Pair<List<Viaje>, Int>>
    suspend fun getViaje(id: Int): Result<Viaje>
    suspend fun createViaje(busId: Int, rutaId: Int): Result<Viaje>
    suspend fun updateViaje(id: Int, busId: Int, rutaId: Int): Result<Viaje>
    suspend fun deleteViaje(id: Int): Result<Unit>
    suspend fun comprarPasaje(viajeId: Int, cantidad: Int = 1): Result<Viaje>
    suspend fun updateStatus(id: Int, status: String): Result<Viaje>
    suspend fun getStats(): Result<Map<String, Any>>
}