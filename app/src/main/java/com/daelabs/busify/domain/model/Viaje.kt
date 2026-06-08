package com.daelabs.busify.domain.model

enum class ViajeStatus(val value: String, val label: String) {
    PENDING("pending", "Pendiente"),
    SCHEDULED("scheduled", "Programado"),
    ON_ROUTE("en_route", "En Ruta"),
    COMPLETED("completed", "Completado"),
    CANCELLED("cancelled", "Cancelado");

    companion object {
        fun fromValue(value: String): ViajeStatus {
            return when (value.lowercase()) {
                "pending", "scheduled" -> PENDING
                "en_route", "on_route", "in_progress", "en_progreso" -> ON_ROUTE
                "completed", "completado" -> COMPLETED
                "cancelled", "cancelado" -> CANCELLED
                else -> PENDING
            }
        }
    }
}

data class ViajePasajero(
    val id: Int,
    val pasajeroId: Int,
    val pasajeroNombre: String,
    val subioEnParadaId: Int,
    val tarifaPagada: Double,
    val fechaRegistro: String,
)

data class Viaje(
    val id: Int,
    val busId: Int?,
    val busPlaca: String,
    val rutaId: Int?,
    val rutaNombre: String,
    val status: ViajeStatus,
    val tarifaTotalRecaudada: Double,
    val numPasajerosTotal: Int,
    val pasajeros: List<ViajePasajero>,
    val createdAt: String,
    val updatedAt: String,
)