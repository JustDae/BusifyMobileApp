package com.daelabs.busify.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusPayload

data class RutaSummaryDto(
    val id: Int,
    @SerializedName(value = "name", alternate = ["nombre", "ruta_name", "text"]) val name: String?,
)

data class BusDto(
    val id: Int,
    @SerializedName("unit_number") val numeroBus: String?,
    @SerializedName("plate") val placa: String?,
    @SerializedName("model") val modelo: String?,
    @SerializedName(value = "route", alternate = ["ruta", "assigned_route", "route_id"]) val rutaIdRaw: Int?,
    @SerializedName(value = "route_obj", alternate = ["route_detail", "ruta_obj"]) val rutaObjeto: RutaSummaryDto?,
    @SerializedName(value = "route_name", alternate = ["ruta_actual", "ruta_nombre", "current_route_name"]) val rutaNombrePlano: String?,
    @SerializedName("capacity") val capacidadPasajeros: Int?,
    @SerializedName("current_passengers") val pasajerosActuales: Int?,
    @SerializedName("speed_kmh") val velocidadKmh: String?,
    @SerializedName("is_on_route") val enRuta: Boolean?,
    @SerializedName("status") val status: String?,
    @SerializedName("is_active") val estaActivo: Boolean?,
    @SerializedName("driver") val choferId: Int?,
    @SerializedName("driver_name") val choferNombre: String?,
    @SerializedName("last_lat") val ultimaUbicacionLat: Double?,
    @SerializedName("last_lng") val ultimaUbicacionLng: Double?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
)

data class BusRequestDto(
    @SerializedName("unit_number") val numeroBus: String,
    @SerializedName("plate") val placa: String,
    @SerializedName("model") val modelo: String,
    @SerializedName("route") val rutaId: Int?,
    @SerializedName("capacity") val capacidadPasajeros: Int,
    @SerializedName("status") val status: String,
    @SerializedName("driver") val choferId: Int?,
)

data class BusStatsDto(
    @SerializedName("total_active") val totalActive: Int?,
    @SerializedName("total_inactive") val totalInactive: Int?,
    @SerializedName("avg_speed") val avgSpeed: Double?,
    @SerializedName("total_capacity") val totalCapacity: Int?,
)

fun BusDto.toDomain() = Bus(
    id = id,
    numeroBus = numeroBus ?: "0",
    placa = placa ?: "Sin Placa",
    rutaId = rutaObjeto?.id ?: rutaIdRaw,
    rutaActual = rutaObjeto?.name ?: rutaNombrePlano ?: (if (rutaIdRaw != null) "Ruta #$rutaIdRaw" else "Sin Ruta"),
    capacidadPasajeros = capacidadPasajeros ?: 0,
    pasajerosActuales = pasajerosActuales ?: 0,
    velocidadKmh = velocidadKmh?.toDoubleOrNull()?.toInt() ?: 0,
    estaActivo = estaActivo ?: (status == "active"),
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
    modelo = modelo,
    rutaId = rutaId,
    capacidadPasajeros = capacidadPasajeros,
    status = if (estaActivo) "active" else "inactive",
    choferId = if (choferId == 0) null else choferId,
)
