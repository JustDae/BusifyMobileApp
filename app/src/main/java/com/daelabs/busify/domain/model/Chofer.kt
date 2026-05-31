package com.daelabs.busify.domain.model

data class Chofer(
    val id: Int,
    val nombreCompleto: String,
    val licenciaConducir: String,
    val telefono: String,
    val estaDisponible: Boolean,
    val busAsignadoId: Int?,
    val busAsignadoNumero: Int?,
    val createdAt: String,
)

data class ChoferPayload(
    val nombreCompleto: String,
    val licenciaConducir: String,
    val telefono: String,
    val estaDisponible: Boolean,
)