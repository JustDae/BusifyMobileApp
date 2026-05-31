package com.daelabs.busify.data.repository

import com.daelabs.busify.data.remote.api.BusApi
import com.daelabs.busify.data.remote.dto.toDomain
import com.daelabs.busify.data.remote.dto.toRequest
import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusFilters
import com.daelabs.busify.domain.model.BusPayload
import com.daelabs.busify.domain.repository.BusRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusRepositoryImpl @Inject constructor(
    private val api: BusApi,
) : BusRepository {

    override suspend fun getBuses(filters: BusFilters): Result<Pair<List<Bus>, Int>> = runCatching {
        val params = buildMap<String, String> {
            filters.search?.let { put("search", it) }
            filters.ruta?.let { put("ruta", it) }
            filters.estaActivo?.let { put("esta_activo", it.toString()) }
            filters.enRuta?.let { put("en_ruta", it.toString()) }
            filters.ordering?.let { put("ordering", it) }
            put("page", filters.page.toString())
            put("page_size", filters.pageSize.toString())
        }
        val response = api.getBuses(params)
        if (response.isSuccessful) {
            val body = response.body()!!
            // Se extraen los datos desde la propiedad .results de PaginatedDto
            Pair(body.results.map { it.toDomain() }, body.count)
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getBus(id: Int): Result<Bus> = runCatching {
        val response = api.getBusById(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createBus(payload: BusPayload): Result<Bus> = runCatching {
        val response = api.createBus(payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateBus(id: Int, payload: BusPayload): Result<Bus> = runCatching {
        val response = api.updateBus(id, payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun deleteBus(id: Int): Result<Unit> = runCatching {
        val response = api.deleteBus(id)
        if (!response.isSuccessful) error("Error ${response.code()}")
    }
}