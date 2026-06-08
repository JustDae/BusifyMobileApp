package com.daelabs.busify.data.remote.api

import com.daelabs.busify.data.remote.dto.AsignarUnidadRequestDto
import com.daelabs.busify.data.remote.dto.CrearDespachoRequestDto
import com.daelabs.busify.data.remote.dto.ViajeDto
import com.daelabs.busify.data.remote.dto.PaginatedDto
import retrofit2.Response
import retrofit2.http.*

interface DespachoApi {

    @POST("viajes/despacho/")
    suspend fun crearDespacho(
        @Body request: CrearDespachoRequestDto
    ): Response<ViajeDto>

    @POST("viajes/{id}/asignar-unidad/")
    suspend fun asignarUnidad(
        @Path("id") viajeId: Int,
        @Body request: AsignarUnidadRequestDto
    ): Response<ViajeDto>

    @POST("viajes/{id}/confirmar-despacho/")
    suspend fun confirmarDespacho(
        @Path("id") viajeId: Int
    ): Response<ViajeDto>

    @GET("viajes/")
    suspend fun getViajesActivos(
        @Query("page") page: Int?,
        @Query("status") status: String?
    ): Response<PaginatedDto<ViajeDto>>
}