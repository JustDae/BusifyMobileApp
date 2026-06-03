package com.daelabs.busify.data.remote.dto

import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaPayload
import com.google.gson.annotations.SerializedName

data class AsignarBusRequestDto(
    @SerializedName("cantidad") val cantidad: Int
)

data class AsignarBusResponseDto(
    @SerializedName("total_buses") val totalBuses: Int
)

data class RutaResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<RutaDto>
)

data class RutaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("tarifa") val tarifa: Double?,
    @SerializedName("total_buses") val totalBuses: Int?,
    @SerializedName("cooperativa_name") val cooperativaName: String?,
    @SerializedName("mapa_snippet_url") val mapaSnippetUrl: String?,
    @SerializedName("has_buses_activos") val hasBusesActivos: Boolean?,
    @SerializedName("max_capacidad_buses") val maxCapacidadBuses: Int?
)

data class RutaStatsDto(
    @SerializedName("total_active") val totalActive: Int,
    @SerializedName("total_inactive") val totalInactive: Int,
    @SerializedName("avg_tarifa") val avgTarifa: Double?,
    @SerializedName("total_buses") val totalBuses: Int?,
    @SerializedName("alertas_criticas") val alertasCriticas: Int
)

data class RutaRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("tarifa") val tarifa: Double,
    @SerializedName("cooperativa") val cooperativa: Int?
)

fun RutaDto.toDomain() = Ruta(
    id = id,
    name = name ?: "Sin Nombre",
    tarifa = tarifa ?: 0.0,
    totalBuses = totalBuses ?: 0,
    cooperativaName = cooperativaName,
    mapaSnippetUrl = mapaSnippetUrl,
    hasBusesActivos = hasBusesActivos ?: false,
    maxCapacidadBuses = maxCapacidadBuses ?: 0
)

fun RutaPayload.toRequest() = RutaRequestDto(
    name = name,
    tarifa = tarifa,
    cooperativa = cooperativaId
)