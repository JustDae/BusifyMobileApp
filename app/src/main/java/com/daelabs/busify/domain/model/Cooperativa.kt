package com.daelabs.busify.domain.model

data class Cooperativa(
    val id: Int,
    val name: String,
    val isActive: Boolean,
    val totalRutas: Int
)