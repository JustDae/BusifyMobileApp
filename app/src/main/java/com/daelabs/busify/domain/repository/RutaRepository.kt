package com.daelabs.busify.domain.repository

import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaPayload

interface RutaRepository {
    suspend fun getRutas(): Result<List<Ruta>>
    suspend fun getRuta(id: Int): Result<Ruta>
    suspend fun createRuta(payload: RutaPayload): Result<Ruta>
    suspend fun updateRuta(id: Int, payload: RutaPayload): Result<Ruta>
    suspend fun deleteRuta(id: Int): Result<Unit>
}