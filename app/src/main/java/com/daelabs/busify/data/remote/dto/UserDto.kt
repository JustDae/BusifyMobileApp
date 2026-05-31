package com.daelabs.busify.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.daelabs.busify.domain.model.User
import com.daelabs.busify.domain.model.UserPayload

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("is_staff") val isStaff: Boolean,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("date_joined") val dateJoined: String,
    @SerializedName("num_viajes_asignados") val numViajesAsignados: Int,
)

data class UserRequestDto(
    val username: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("is_staff") val isStaff: Boolean,
    @SerializedName("is_active") val isActive: Boolean,
    val password: String? = null,
)

data class ToggleActiveResponseDto(
    val message: String,
    @SerializedName("is_active") val isActive: Boolean,
)

data class UserStatsDto(
    val total: Int,
    val active: Int,
    val inactive: Int,
    val staff: Int,
)


fun UserDto.toDomain() = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    isStaff = isStaff,
    isActive = isActive,
    dateJoined = dateJoined,
    numViajes = numViajesAsignados,
)

fun UserPayload.toRequest() = UserRequestDto(
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    isStaff = isStaff,
    isActive = isActive,
    password = password,
)