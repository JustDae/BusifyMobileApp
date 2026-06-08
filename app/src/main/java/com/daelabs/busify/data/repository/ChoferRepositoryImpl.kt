package com.daelabs.busify.data.repository

import com.daelabs.busify.data.remote.api.ChoferApi
import com.daelabs.busify.data.remote.dto.toDomain
import com.daelabs.busify.data.remote.dto.toRequest
import com.daelabs.busify.domain.model.Chofer
import com.daelabs.busify.domain.model.ChoferPayload
import com.daelabs.busify.domain.repository.ChoferRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChoferRepositoryImpl @Inject constructor(
    private val api: ChoferApi,
) : ChoferRepository {

    override suspend fun getChoferes(search: String?): Result<List<Chofer>> = runCatching {
        val response = api.getChoferes(search)
        if (response.isSuccessful) response.body()!!.results.map { it.toDomain() }
        else error("Error ${response.code()}")
    }

    override suspend fun getChofer(id: Int): Result<Chofer> = runCatching {
        val response = api.getChofer(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createChofer(payload: ChoferPayload): Result<Chofer> = runCatching {
        val response = api.createChofer(payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun updateChofer(id: Int, payload: ChoferPayload): Result<Chofer> = runCatching {
        val response = api.updateChofer(id, payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun deleteChofer(id: Int): Result<Unit> = runCatching {
        val response = api.deleteChofer(id)
        if (!response.isSuccessful) error("Error ${response.code()}")
    }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        val response = api.getStats()
        if (response.isSuccessful) {
            val s = response.body()!!
            mapOf(
                "total" to (s.total ?: 0),
                "available" to (s.activeStaff ?: 0),
                "busy" to (s.inactiveStaff ?: 0)
            )
        } else error("Error ${response.code()}")
    }
}