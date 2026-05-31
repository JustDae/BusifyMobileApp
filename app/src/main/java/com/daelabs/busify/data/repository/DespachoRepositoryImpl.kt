package com.daelabs.busify.data.repository

import com.daelabs.busify.data.remote.api.DespachoApi
import com.daelabs.busify.data.remote.dto.CrearDespachoRequestDto
import com.daelabs.busify.data.remote.dto.toDomain
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.model.ViajeStatus
import com.daelabs.busify.domain.repository.DespachoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DespachoRepositoryImpl @Inject constructor(
    private val api: DespachoApi,
) : DespachoRepository {

    override suspend fun crearDespacho(rutaId: Int, busesDespachados: Int): Result<Viaje> = runCatching {
        val request = CrearDespachoRequestDto(
            rutaId = rutaId,
            passengerCount = 0,
            status = "scheduled"
        )
        val response = api.crearDespacho(request)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun asignarUnidad(viajeId: Int, rutaId: Int, busId: Int, choferId: Int): Result<Viaje> = runCatching {
        Viaje(
            id = viajeId,
            status = ViajeStatus.PENDING,
            busPlaca = "",
            tarifaTotalRecaudada = 0.0,
            numPasajerosTotal = 0,
            pasajeros = emptyList(),
            createdAt = "",
            updatedAt = ""
        )
    }

    override suspend fun confirmarDespacho(viajeId: Int): Result<Viaje> = runCatching {
        Viaje(
            id = viajeId,
            status = ViajeStatus.PENDING,
            busPlaca = "",
            tarifaTotalRecaudada = 0.0,
            numPasajerosTotal = 0,
            pasajeros = emptyList(),
            createdAt = "",
            updatedAt = ""
        )
    }

    override suspend fun getViajesActivos(page: Int?): Result<Pair<List<Viaje>, Int>> = runCatching {
        val response = api.getViajesActivos(page)
        if (response.isSuccessful) {
            val body = response.body()!!
            Pair(body.results.map { it.toDomain() }, body.count)
        } else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }
}