package com.daelabs.busify.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusPayload

data class RutaSummaryDto(
    val id: Int,
    val name: String,
)

data class BusDto(
    val id: Int,
    @SerializedName("numero_bus") val numeroBus: Int?,
    val placa: String?,
    @SerializedName("ruta_actual") val rutaActual: String?,
    @SerializedName("capacidad_pasajeros") val capacidadPasajeros: Int?,
    @SerializedName("pasajeros_actuales") val pasajerosActuales: Int?,
    @SerializedName("velocidad_kmh") val velocidadKmh: String?,
    @SerializedName("en_ruta") val enRuta: Boolean?,
    @SerializedName("esta_activo") val estaActivo: Boolean?,
    @SerializedName("chofer_id") val choferId: Int?,
    @SerializedName("chofer_nombre") val choferNombre: String?,
    @SerializedName("ultima_ubicacion_lat") val ultimaUbicacionLat: Double?,
    @SerializedName("ultima_ubicacion_lng") val ultimaUbicacionLng: Double?,
    val ruta: RutaSummaryDto?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
)

data class BusRequestDto(
    @SerializedName("numero_bus") val numeroBus: Int,
    val placa: String,
    @SerializedName("ruta_actual") val rutaActual: String,
    @SerializedName("capacidad_pasajeros") val capacidadPasajeros: Int,
    @SerializedName("esta_activo") val estaActivo: Boolean,
    @SerializedName("chofer_id") val choferId: Int,
)

data class BusStatsDto(
    @SerializedName("total_active") val totalActive: Int,
    @SerializedName("total_inactive") val totalInactive: Int,
    @SerializedName("avg_speed") val avgSpeed: Double?,
    @SerializedName("total_capacity") val totalCapacity: Int?,
)

fun BusDto.toDomain() = Bus(
    id = id,
    numeroBus = numeroBus ?: 0,
    placa = placa ?: "Sin Placa",
    rutaActual = rutaActual ?: "Sin Ruta",
    capacidadPasajeros = capacidadPasajeros ?: 0,
    pasajerosActuales = pasajerosActuales ?: 0,
    velocidadKmh = velocidadKmh?.toDoubleOrNull()?.toInt() ?: 0,
    estaActivo = estaActivo ?: false,
    enRuta = enRuta ?: false,
    choferId = choferId,
    choferNombre = choferNombre,
    ultimaUbicacionLat = ultimaUbicacionLat,
    ultimaUbicacionLng = ultimaUbicacionLng,
    createdAt = createdAt ?: "",
    updatedAt = updatedAt ?: "",
)

fun BusPayload.toRequest() = BusRequestDto(
    numeroBus = numeroBus,
    placa = placa,
    rutaActual = rutaActual,
    capacidadPasajeros = capacidadPasajeros,
    estaActivo = estaActivo,
    choferId = choferId,
)