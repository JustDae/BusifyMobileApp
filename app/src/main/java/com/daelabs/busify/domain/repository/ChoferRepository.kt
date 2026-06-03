package com.daelabs.busify.domain.repository

import com.daelabs.busify.domain.model.Chofer
import com.daelabs.busify.domain.model.ChoferPayload

interface ChoferRepository {
    suspend fun getChoferes(search: String? = null): Result<List<Chofer>>
    suspend fun getChofer(id: Int): Result<Chofer>
    suspend fun createChofer(payload: ChoferPayload): Result<Chofer>
    suspend fun updateChofer(id: Int, payload: ChoferPayload): Result<Chofer>
    suspend fun deleteChofer(id: Int): Result<Unit>
    suspend fun getStats(): Result<Map<String, Any>>
}