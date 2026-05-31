package com.daelabs.busify.data.repository

import com.daelabs.busify.data.remote.api.RutaApi
import com.daelabs.busify.data.remote.dto.AsignarBusRequestDto
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaFilters
import com.daelabs.busify.domain.model.RutaPayload
import com.daelabs.busify.domain.repository.RutaRepository
import com.daelabs.busify.data.remote.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RutaRepositoryImpl @Inject constructor(
    private val api: RutaApi,
) : RutaRepository {

    override suspend fun getRutas(filters: RutaFilters): Result<Pair<List<Ruta>, Int>> =
        runCatching {
            val params = buildMap<String, String> {
                filters.search?.let   { put("search",    it) }
                filters.cooperativa?.let { put("cooperativa",  it.toString()) }
                filters.tarifaMin?.let { put("tarifa_min", it.toString()) }
                filters.tarifaMax?.let { put("tarifa_max", it.toString()) }
                filters.busesMin?.let { put("buses_min", it.toString()) }
                filters.isActive?.let { put("is_active", it.toString()) }
                filters.ordering?.let { put("ordering",  it) }
                put("page",      filters.page.toString())
                put("page_size", filters.pageSize.toString())
            }
            val response = api.getRutas(params)
            if (response.isSuccessful) {
                val body = response.body()!!
                Pair(body.results.map { it.toDomain() }, body.count)
            } else error("Error ${response.code()}")
        }

    override suspend fun getRuta(id: Int): Result<Ruta> = runCatching {
        val response = api.getRuta(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createRuta(payload: RutaPayload): Result<Ruta> = runCatching {
        val response = api.createRuta(payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateRuta(id: Int, payload: RutaPayload): Result<Ruta> =
        runCatching {
            val response = api.updateRuta(id, payload.toRequest())
            if (response.isSuccessful) response.body()!!.toDomain()
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun deleteRuta(id: Int): Result<Unit> = runCatching {
        val response = api.deleteRuta(id)
        if (!response.isSuccessful) error("Error ${response.code()}")
    }

    override suspend fun asignarBus(id: Int, cantidad: Int): Result<Int> = runCatching {
        val response = api.asignarBus(id, AsignarBusRequestDto(cantidad))
        if (response.isSuccessful) response.body()!!.totalBuses
        else error("Error ${response.code()}")
    }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        val response = api.getStats()
        if (response.isSuccessful) {
            val s = response.body()!!
            mapOf(
                "total_active"   to s.totalActive,
                "total_inactive" to s.totalInactive,
                "avg_tarifa"     to (s.avgTarifa ?: 0.0),
                "total_buses"    to (s.totalBuses ?: 0),
                "alertas_criticas" to s.alertasCriticas,
            )
        } else error("Error ${response.code()}")
    }
}