package com.daelabs.busify.domain.repository

import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaFilters
import com.daelabs.busify.domain.model.RutaPayload

interface RutaRepository {
    suspend fun getRutas(filters: RutaFilters): Result<Pair<List<Ruta>, Int>>
    suspend fun getRuta(id: Int): Result<Ruta>
    suspend fun createRuta(payload: RutaPayload): Result<Ruta>
    suspend fun updateRuta(id: Int, payload: RutaPayload): Result<Ruta>
    suspend fun deleteRuta(id: Int): Result<Unit>
    suspend fun asignarBus(id: Int, quantity: Int): Result<Int>
    suspend fun getStats(): Result<Map<String, Any>>
}