package com.daelabs.busify.domain.model

data class Chofer(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val licenseNumber: String,
    val dailyRate: Double,
    val tripsCompleted: Int,
    val isActive: Boolean,
    val createdAt: String,
)

data class ChoferPayload(
    val firstName: String,
    val lastName: String,
    val licenseNumber: String,
    val dailyRate: Double,
    val isActive: Boolean,
)