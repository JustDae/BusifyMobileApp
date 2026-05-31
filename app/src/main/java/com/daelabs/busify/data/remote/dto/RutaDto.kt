package com.daelabs.busify.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.domain.model.RutaPayload

data class RutaDto(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("total_buses") val totalBuses: Int,
    @SerializedName("created_at") val createdAt: String,
)

data class RutaRequestDto(
    val name: String,
    val slug: String,
    val description: String,
    @SerializedName("is_active") val isActive: Boolean,
)

data class RutaStatsDto(
    val total: Int,
    val active: Int,
    val inactive: Int,
)

fun RutaDto.toDomain() = Ruta(
    id = id,
    name = name,
    slug = slug,
    description = description,
    isActive = isActive,
    totalBuses = totalBuses,
    createdAt = createdAt,
)

fun RutaPayload.toRequest() = RutaRequestDto(
    name = name,
    slug = slug,
    description = description,
    isActive = isActive,
)