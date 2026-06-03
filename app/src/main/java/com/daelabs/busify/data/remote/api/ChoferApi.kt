package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.ChoferDto
import com.daelabs.busify.data.remote.dto.ChoferRequestDto
import com.daelabs.busify.data.remote.dto.ChoferStatsDto
import com.daelabs.busify.data.remote.dto.PaginatedDto
import retrofit2.Response
import retrofit2.http.*

interface ChoferApi {
    @GET("choferes/")
    suspend fun getChoferes(@Query("search") search: String? = null): Response<PaginatedDto<ChoferDto>>

    @GET("choferes/{id}/")
    suspend fun getChofer(@Path("id") id: Int): Response<ChoferDto>

    @POST("choferes/")
    suspend fun createChofer(@Body body: ChoferRequestDto): Response<ChoferDto>

    @PUT("choferes/{id}/")
    suspend fun updateChofer(@Path("id") id: Int, @Body body: ChoferRequestDto): Response<ChoferDto>

    @DELETE("choferes/{id}/")
    suspend fun deleteChofer(@Path("id") id: Int): Response<Unit>

    @GET("choferes/stats/")
    suspend fun getStats(): Response<ChoferStatsDto>
}