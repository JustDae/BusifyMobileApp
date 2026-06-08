package com.daelabs.busify.data.repository

import com.daelabs.busify.data.remote.api.ViajeApi
import com.daelabs.busify.data.remote.dto.toDomain
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.repository.ViajeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViajeRepositoryImpl @Inject constructor(
    private val api: ViajeApi,
) : ViajeRepository {

    override suspend fun getViajes(page: Int?, status: String?): Result<Pair<List<Viaje>, Int>> = runCatching {
        val response = api.getViajes(page, status)
        if (response.isSuccessful) {
            val body = response.body()!!
            Pair(body.results.map { it.toDomain() }, body.count)
        } else error("Error ${response.code()}")
    }

    override suspend fun getViaje(id: Int): Result<Viaje> = runCatching {
        val response = api.getViaje(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createViaje(busId: Int, rutaId: Int): Result<Viaje> = runCatching {
        val response = api.createViaje(com.daelabs.busify.data.remote.dto.CreateViajeRequestDto(busId, rutaId))
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun updateViaje(id: Int, busId: Int, rutaId: Int): Result<Viaje> = runCatching {
        val response = api.updateViaje(id, com.daelabs.busify.data.remote.dto.CreateViajeRequestDto(busId, rutaId))
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun deleteViaje(id: Int): Result<Unit> = runCatching {
        val response = api.deleteViaje(id)
        if (!response.isSuccessful) error("Error ${response.code()}")
    }

    override suspend fun comprarPasaje(viajeId: Int, cantidad: Int): Result<Viaje> = runCatching {
        val response = api.comprarPasaje(viajeId, cantidad)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateStatus(id: Int, status: String): Result<Viaje> = runCatching {
        val response = api.updateStatus(id, com.daelabs.busify.data.remote.dto.UpdateStatusRequestDto(status))
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        val response = api.getStats()
        if (response.isSuccessful) {
            val s = response.body()!!
            mapOf(
                "total_viajes" to (s.totalViajes ?: 0),
                "by_status" to (s.byStatus ?: emptyMap<String, Int>())
            )
        } else error("Error ${response.code()}")
    }
}