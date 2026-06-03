package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface BusApi {

    @GET("buses/")
    suspend fun getBuses(
        @QueryMap filters: Map<String, String>
    ): Response<PaginatedDto<BusDto>>

    @GET("buses/{id}/")
    suspend fun getBusById(@Path("id") id: Int): Response<BusDto>

    @POST("buses/")
    suspend fun createBus(@Body body: BusRequestDto): Response<BusDto>

    @PATCH("buses/{id}/")
    suspend fun updateBus(
        @Path("id") id: Int,
        @Body body: BusRequestDto
    ): Response<BusDto>

    @DELETE("buses/{id}/")
    suspend fun deleteBus(@Path("id") id: Int): Response<Unit>

    @GET("buses/stats/")
    suspend fun getStats(): Response<BusStatsDto>
}