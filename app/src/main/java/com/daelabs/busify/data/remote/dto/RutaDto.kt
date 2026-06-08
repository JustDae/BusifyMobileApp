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
    @SerializedName("description") val description: String?,
    @SerializedName("origin") val origin: String?,
    @SerializedName("destination") val destination: String?,
    @SerializedName("base_fare") val baseFare: Double?,
    @SerializedName("tarifa") val tarifa: Double?,
    @SerializedName("total_buses") val totalBuses: Int?,
    @SerializedName("total_paradas") val totalParadas: Int?,
    @SerializedName("cooperativa") val cooperativa: Int?,
    @SerializedName("cooperativa_name") val cooperativaName: String?,
    @SerializedName("mapa_snippet_url") val mapaSnippetUrl: String?,
    @SerializedName("has_buses_activos") val hasBusesActivos: Boolean?,
    @SerializedName("is_active") val isActive: Boolean?,
    @SerializedName("max_capacidad_buses") val maxCapacidadBuses: Int?,
    @SerializedName("created_at") val createdAt: String?
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
    @SerializedName("description") val description: String,
    @SerializedName("origin") val origin: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("base_fare") val tarifa: Double,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("cooperativa") val cooperativa: Int?
)

fun RutaDto.toDomain() = Ruta(
    id = id,
    name = name ?: "Sin Nombre",
    description = description ?: "",
    origin = origin ?: "",
    destination = destination ?: "",
    tarifa = tarifa ?: baseFare ?: 0.0,
    totalBuses = totalBuses ?: 0,
    totalParadas = totalParadas ?: 0,
    cooperativaName = cooperativaName,
    cooperativaId = cooperativa,
    mapaSnippetUrl = mapaSnippetUrl,
    hasBusesActivos = hasBusesActivos ?: false,
    isActive = isActive ?: true,
    maxCapacidadBuses = maxCapacidadBuses ?: 0
)

fun RutaPayload.toRequest() = RutaRequestDto(
    name = name,
    description = description,
    origin = origin,
    destination = destination,
    tarifa = tarifa,
    isActive = isActive,
    cooperativa = cooperativaId
)