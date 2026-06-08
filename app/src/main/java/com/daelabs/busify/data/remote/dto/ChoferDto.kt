package com.daelabs.busify.data.remote.dto

import com.daelabs.busify.domain.model.Chofer
import com.daelabs.busify.domain.model.ChoferPayload
import com.google.gson.annotations.SerializedName

data class ChoferDto(
    val id: Int,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("license_number") val licenseNumber: String?,
    @SerializedName("daily_rate") val dailyRate: String?,
    @SerializedName("trips_completed") val tripsCompleted: Int?,
    @SerializedName("is_active") val isActive: Boolean?,
    @SerializedName("created_at") val createdAt: String?,
)

data class ChoferRequestDto(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("license_number") val licenseNumber: String,
    @SerializedName("daily_rate") val dailyRate: Double,
    @SerializedName("is_active") val isActive: Boolean,
)

data class ChoferStatsDto(
    val total: Int?,
    @SerializedName("active_staff") val activeStaff: Int?,
    @SerializedName("inactive_staff") val inactiveStaff: Int?,
)

fun ChoferDto.toDomain() = Chofer(
    id = id,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    fullName = fullName ?: "",
    licenseNumber = licenseNumber ?: "N/A",
    dailyRate = dailyRate?.toDoubleOrNull() ?: 0.0,
    tripsCompleted = tripsCompleted ?: 0,
    isActive = isActive ?: false,
    createdAt = createdAt ?: ""
)

fun ChoferPayload.toRequest() = ChoferRequestDto(
    firstName = firstName,
    lastName = lastName,
    licenseNumber = licenseNumber,
    dailyRate = dailyRate,
    isActive = isActive
)