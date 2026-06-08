package com.daelabs.busify.domain.model

data class Ruta(
    val id: Int,
    val name: String,
    val description: String,
    val origin: String,
    val destination: String,
    val tarifa: Double,
    val totalBuses: Int,
    val totalParadas: Int,
    val cooperativaName: String?,
    val cooperativaId: Int?,
    val mapaSnippetUrl: String?,
    val hasBusesActivos: Boolean,
    val isActive: Boolean,
    val maxCapacidadBuses: Int
)

data class RutaFilters(
    val search: String? = null,
    val cooperativa: Int? = null,
    val tarifaMin: Double? = null,
    val tarifaMax: Double? = null,
    val busesMin: Int? = null,
    val isActive: Boolean? = null,
    val ordering: String? = null,
    val page: Int = 1,
    val pageSize: Int = 12
)

data class RutaPayload(
    val name: String,
    val description: String,
    val origin: String,
    val destination: String,
    val tarifa: Double,
    val isActive: Boolean,
    val cooperativaId: Int?
)