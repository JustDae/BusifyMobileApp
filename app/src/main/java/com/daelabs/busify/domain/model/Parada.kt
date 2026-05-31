package com.daelabs.busify.domain.model

data class Parada(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val secuenciaEnRuta: Int,
    val rutaId: Int?,
    val rutaNombre: String?,
    val createdAt: String,
)

data class ParadaPayload(
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val secuenciaEnRuta: Int,
    val rutaId: Int,
)