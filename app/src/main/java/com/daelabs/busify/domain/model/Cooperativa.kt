package com.daelabs.busify.domain.model

import com.google.gson.annotations.SerializedName

data class Cooperativa(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("total_rutas") val totalRutas: Int
)