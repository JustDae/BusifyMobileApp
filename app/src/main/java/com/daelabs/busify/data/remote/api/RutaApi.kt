package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RutaApi {
    @GET("rutas/")
    suspend fun getRutas(@QueryMap options: Map<String, String>): Response<RutaResponseDto>

    @GET("rutas/{id}/")
    suspend fun getRuta(@Path("id") id: Int): Response<RutaDto>

    @POST("rutas/")
    suspend fun createRuta(@Body request: RutaRequestDto): Response<RutaDto>

    @PUT("rutas/{id}/")
    suspend fun updateRuta(@Path("id") id: Int, @Body request: RutaRequestDto): Response<RutaDto>

    @DELETE("rutas/{id}/")
    suspend fun deleteRuta(@Path("id") id: Int): Response<Unit>

    @POST("rutas/{id}/asignar_bus/")
    suspend fun asignarBus(@Path("id") id: Int, @Body request: AsignarBusRequestDto): Response<AsignarBusResponseDto>

    @GET("rutas/stats/")
    suspend fun getStats(): Response<RutaStatsDto>
}