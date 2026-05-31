package com.daelabs.busify.domain.model

data class Ruta(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String,
    val isActive: Boolean,
    val totalBuses: Int,
    val createdAt: String,
)

data class RutaPayload(
    val name: String,
    val slug: String,
    val description: String,
    val isActive: Boolean,
)