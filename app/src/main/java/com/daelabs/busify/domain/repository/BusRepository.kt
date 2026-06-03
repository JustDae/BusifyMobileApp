package com.daelabs.busify.domain.repository

import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusFilters
import com.daelabs.busify.domain.model.BusPayload

interface BusRepository {
    suspend fun getBuses(filters: BusFilters): Result<Pair<List<Bus>, Int>>
    suspend fun getBus(id: Int): Result<Bus>
    suspend fun createBus(payload: BusPayload): Result<Bus>
    suspend fun updateBus(id: Int, payload: BusPayload): Result<Bus>
    suspend fun deleteBus(id: Int): Result<Unit>
    suspend fun getStats(): Result<Map<String, Any>>
}