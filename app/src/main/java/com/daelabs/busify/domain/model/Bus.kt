package com.daelabs.busify.domain.model

data class Bus(
    val id: Int,
    val numeroBus: Int,
    val placa: String,
    val rutaActual: String,
    val capacidadPasajeros: Int,
    val pasajerosActuales: Int,
    val velocidadKmh: Int,
    val estaActivo: Boolean,
    val enRuta: Boolean,
    val choferId: Int?,
    val choferNombre: String?,
    val ultimaUbicacionLat: Double?,
    val ultimaUbicacionLng: Double?,
    val createdAt: String,
    val updatedAt: String,
)

data class BusPayload(
    val numeroBus: Int,
    val placa: String,
    val rutaActual: String,
    val capacidadPasajeros: Int,
    val estaActivo: Boolean,
    val choferId: Int,
)

data class BusFilters(
    val search: String? = null,
    val ruta: String? = null,
    val estaActivo: Boolean? = null,
    val enRuta: Boolean? = null,
    val sobrepasaCapacidad: Boolean? = null,
    val ordering: String? = null,
    val page: Int = 1,
    val pageSize: Int = 12,
)