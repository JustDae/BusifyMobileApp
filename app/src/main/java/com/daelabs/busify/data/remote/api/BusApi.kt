package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.BusDto
import com.daelabs.busify.data.remote.dto.BusRequestDto
import retrofit2.Response
import retrofit2.http.*

interface BusApi {

    @GET("api/v1/buses")
    suspend fun getBuses(
        @QueryMap options: Map<String, String>
    ): Response<List<BusDto>>

    @GET("api/v1/buses/{id}")
    suspend fun getBusById(
        @Path("id") id: Int
    ): Response<BusDto>

    @POST("api/v1/buses")
    suspend fun createBus(
        @Body payload: BusRequestDto
    ): Response<BusDto>

    @PUT("api/v1/buses/{id}")
    suspend fun updateBus(
        @Path("id") id: Int,
        @Body payload: BusRequestDto
    ): Response<BusDto>

    @DELETE("api/v1/buses/{id}")
    suspend fun deleteBus(
        @Path("id") id: Int
    ): Response<Unit>
}