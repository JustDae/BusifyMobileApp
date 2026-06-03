package com.daelabs.busify.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.model.ViajePasajero
import com.daelabs.busify.domain.model.ViajeStatus

data class BusInViajeDto(
    val id: Int,
    val placa: String,
    @SerializedName("numero_bus") val numeroBus: Int,
)

data class ViajePasajeroDto(
    val id: Int,
    @SerializedName("parada_id") val paradaId: Int,
    @SerializedName("parada_nombre") val paradaNombre: String,
    @SerializedName("pasajeros_subieron") val pasajerosSubieron: Int,
    @SerializedName("pasajeros_bajaron") val pasajerosBajaron: Int,
    @SerializedName("hora_paso") val horaPaso: String,
)

data class ViajeDto(
    val id: Int,
    val bus: BusInViajeDto?,
    val status: String,
    @SerializedName("tarifa_total_recaudada") val tarifaTotalRecaudada: Double?,
    @SerializedName("num_pasajeros_total") val numPasajerosTotal: Int?,
    val pasajeros: List<ViajePasajeroDto>?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class CreateViajeRequestDto(
    @SerializedName("bus_id") val busId: Int,
    @SerializedName("ruta_id") val rutaId: Int,
)

data class AddRegistroRequestDto(
    @SerializedName("parada_id") val paradaId: Int,
    val pasajerosSubieron: Int,
    val pasajerosBajaron: Int,
)

data class UpdateStatusRequestDto(
    val status: String,
)

data class ViajeStatsDto(
    @SerializedName("total_viajes") val totalViajes: Int,
    @SerializedName("by_status") val byStatus: Map<String, Int>,
)



fun ViajePasajeroDto.toDomain() = ViajePasajero(
    id = id,
    pasajeroId = id,
    pasajeroNombre = paradaNombre,
    subioEnParadaId = paradaId,
    tarifaPagada = 0.0,
    fechaRegistro = horaPaso
)

fun ViajeDto.toDomain() = Viaje(
    id = id,
    busPlaca = bus?.placa ?: "Sin Placa",
    status = ViajeStatus.fromValue(status),
    tarifaTotalRecaudada = tarifaTotalRecaudada ?: 0.0,
    numPasajerosTotal = numPasajerosTotal ?: 0,
    pasajeros = pasajeros?.map { it.toDomain() } ?: emptyList(),
    createdAt = createdAt ?: "",
    updatedAt = updatedAt ?: ""
)