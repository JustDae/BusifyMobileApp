package com.daelabs.busify.domain.model

enum class ViajeStatus(val value: String, val label: String) {
    PENDING("pending", "Pendiente"),
    EN_PROGRESO("en_progreso", "En Progreso"),
    COMPLETADO("completado", "Completado"),
    CANCELADO("cancelado", "Cancelado");

    companion object {
        fun fromValue(value: String): ViajeStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
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
    val busPlaca: String,
    val status: ViajeStatus,
    val tarifaTotalRecaudada: Double,
    val numPasajerosTotal: Int,
    val pasajeros: List<ViajePasajero>,
    val createdAt: String,
    val updatedAt: String,
)