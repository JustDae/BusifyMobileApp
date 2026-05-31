package com.daelabs.busify.data.repository

import com.daelabs.busify.data.remote.api.RutaApi
import com.daelabs.busify.data.remote.dto.toDomain
import com.daelabs.busify.data.remote.dto.toRequest
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaPayload
import com.daelabs.busify.domain.repository.RutaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RutaRepositoryImpl @Inject constructor(
    private val api: RutaApi,
) : RutaRepository {

    override suspend fun getRutas(): Result<List<Ruta>> = runCatching {
        val response = api.getRutas()
        if (response.isSuccessful) {
            response.body()!!.results.map { it.toDomain() }
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getRuta(id: Int): Result<Ruta> = runCatching {
        val response = api.getRutaById(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createRuta(payload: RutaPayload): Result<Ruta> = runCatching {
        val response = api.createRuta(payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateRuta(id: Int, payload: RutaPayload): Result<Ruta> = runCatching {
        val response = api.updateRuta(id, payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun deleteRuta(id: Int): Result<Unit> = runCatching {
        val response = api.deleteRuta(id)
        if (!response.isSuccessful) error("Error ${response.code()}")
    }
}