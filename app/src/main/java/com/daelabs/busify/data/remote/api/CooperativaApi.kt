package com.daelabs.busify.data.remote.api

import com.daelabs.busify.domain.model.Cooperativa
import retrofit2.Response
import retrofit2.http.GET

interface CooperativaApi {
    @GET("cooperativas/")
    suspend fun getCooperativas(): Response<List<Cooperativa>>
}