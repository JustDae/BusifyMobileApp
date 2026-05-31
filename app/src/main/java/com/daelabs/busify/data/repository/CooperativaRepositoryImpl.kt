package com.daelabs.busify.data.repository

import com.daelabs.busify.data.remote.api.CooperativaApi
import com.daelabs.busify.domain.model.Cooperativa
import com.daelabs.busify.domain.repository.CooperativaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CooperativaRepositoryImpl @Inject constructor(
    private val api: CooperativaApi,
) : CooperativaRepository {

    override suspend fun getCooperativas(): Result<List<Cooperativa>> = runCatching {
        val response = api.getCooperativas()
        if (response.isSuccessful) response.body()!!
        else error("Error ${response.code()}")
    }
}