package com.daelabs.busify.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.daelabs.busify.domain.model.Bus
import com.daelabs.busify.domain.model.BusPayload

data class BusDto(
    @SerializedName("id") val id: Int,
    @SerializedName("numero_bus") val numeroBus: Int,
    @SerializedName("placa") val placa: String,
    @SerializedName("ruta_actual") val rutaActual: String,
    @SerializedName("capacidad_pasajeros") val capacidadPasajeros: Int,
    @SerializedName("pasajeros_actuales") val pasajerosActuales: Int,
    @SerializedName("velocidad_kmh") val velocidadKmh: Int,
    @SerializedName("esta_activo") val estaActivo: Boolean,
    @SerializedName("en_ruta") val enRuta: Boolean,
    @SerializedName("chofer_id") val choferId: Int?,
    @SerializedName("chofer_nombre") val choferNombre: String?,
    @SerializedName("ultima_ubicacion_lat") val ultimaUbicacionLat: Double?,
    @SerializedName("ultima_ubicacion_lng") val ultimaUbicacionLng: Double?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class BusRequestDto(
    @SerializedName("numero_bus") val numeroBus: Int,
    @SerializedName("placa") val placa: String,
    @SerializedName("ruta_actual") val rutaActual: String,
    @SerializedName("capacidad_pasajeros") val capacidadPasajeros: Int,
    @SerializedName("esta_activo") val estaActivo: Boolean,
    @SerializedName("chofer_id") val choferId: Int
)

fun BusDto.toDomain() = Bus(
    id = id,
    numeroBus = numeroBus,
    placa = placa,
    rutaActual = rutaActual,
    capacidadPasajeros = capacidadPasajeros,
    pasajerosActuales = pasajerosActuales,
    velocidadKmh = velocidadKmh,
    estaActivo = estaActivo,
    enRuta = enRuta,
    choferId = choferId,
    choferNombre = choferNombre,
    ultimaUbicacionLat = ultimaUbicacionLat,
    ultimaUbicacionLng = ultimaUbicacionLng,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun BusPayload.toRequest() = BusRequestDto(
    numeroBus = numeroBus,
    placa = placa,
    rutaActual = rutaActual,
    capacidadPasajeros = capacidadPasajeros,
    estaActivo = estaActivo,
    choferId = choferId
)