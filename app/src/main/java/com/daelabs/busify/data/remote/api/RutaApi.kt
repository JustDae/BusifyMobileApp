package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.RutaDto
import com.daelabs.busify.data.remote.dto.RutaRequestDto
import com.daelabs.busify.data.remote.dto.RutaStatsDto
import retrofit2.Response
import retrofit2.http.*

interface RutaApi {

    @GET("api/v1/rutas")
    suspend fun getRutas(
        @Query("search") search: String? = null,
        @Query("is_active") isActive: Boolean? = null,
        @Query("ordering") ordering: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 12
    ): Response<List<RutaDto>>

    @GET("api/v1/rutas/{id}")
    suspend fun getRutaById(
        @Path("id") id: Int
    ): Response<RutaDto>

    @POST("api/v1/rutas")
    suspend fun createRuta(
        @Body payload: RutaRequestDto
    ): Response<RutaDto>

    @PUT("api/v1/rutas/{id}")
    suspend fun updateRuta(
        @Path("id") id: Int,
        @Body payload: RutaRequestDto
    ): Response<RutaDto>

    @DELETE("api/v1/rutas/{id}")
    suspend fun deleteRuta(
        @Path("id") id: Int
    ): Response<Unit>

    @GET("api/v1/rutas/stats")
    suspend fun getRutaStats(): Response<RutaStatsDto>
}