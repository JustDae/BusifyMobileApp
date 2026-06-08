package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ViajeApi {
    @GET("viajes/")
    suspend fun getViajes(
        @Query("page") page: Int? = null,
        @Query("status") status: String? = null,
    ): Response<PaginatedDto<ViajeDto>>

    @GET("viajes/{id}/")
    suspend fun getViaje(@Path("id") id: Int): Response<ViajeDto>

    @POST("viajes/")
    suspend fun createViaje(
        @Body body: CreateViajeRequestDto
    ): Response<ViajeDto>

    @PUT("viajes/{id}/")
    suspend fun updateViaje(
        @Path("id") id: Int,
        @Body body: CreateViajeRequestDto
    ): Response<ViajeDto>

    @DELETE("viajes/{id}/")
    suspend fun deleteViaje(@Path("id") id: Int): Response<Unit>

    @POST("viajes/{id}/add-registro/")
    suspend fun addRegistro(
        @Path("id") id: Int,
        @Body body: AddRegistroRequestDto,
    ): Response<ViajeDto>

    @POST("viajes/{id}/comprar-pasaje/")
    suspend fun comprarPasaje(
        @Path("id") id: Int,
        @Query("cantidad") cantidad: Int = 1
    ): Response<ViajeDto>

    @POST("viajes/{id}/update-status/")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body body: UpdateStatusRequestDto,
    ): Response<ViajeDto>

    @GET("viajes/stats/")
    suspend fun getStats(): Response<ViajeStatsDto>
}