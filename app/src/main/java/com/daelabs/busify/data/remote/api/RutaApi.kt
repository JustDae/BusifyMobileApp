package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RutaApi {
    @GET("rutas/")
    suspend fun getRutas(): Response<PaginatedDto<RutaDto>>

    @GET("rutas/{id}/")
    suspend fun getRutaById(@Path("id") id: Int): Response<RutaDto>

    @POST("rutas/")
    suspend fun createRuta(@Body body: RutaRequestDto): Response<RutaDto>

    @PATCH("rutas/{id}/")
    suspend fun updateRuta(
        @Path("id") id: Int,
        @Body body: RutaRequestDto,
    ): Response<RutaDto>

    @DELETE("rutas/{id}/")
    suspend fun deleteRuta(@Path("id") id: Int): Response<Unit>

    @GET("rutas/stats/")
    suspend fun getStats(): Response<RutaStatsDto>
}