package com.daelabs.busify.domain.repository

import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.model.Ruta

interface DespachoRepository {
    suspend fun crearDespacho(rutaId: Int, busesDespachados: Int): Result<Viaje>
    suspend fun asignarUnidad(viajeId: Int, rutaId: Int, busId: Int, choferId: Int): Result<Viaje>
    suspend fun confirmarDespacho(viajeId: Int): Result<Viaje>
    suspend fun getViajesActivos(page: Int? = null, status: String? = null): Result<Pair<List<Viaje>, Int>>
}