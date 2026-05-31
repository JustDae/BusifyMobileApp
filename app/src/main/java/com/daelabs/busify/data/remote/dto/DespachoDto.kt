package com.daelabs.busify.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AsignarUnidadRequestDto(
    @SerializedName("ruta_id") val rutaId: Int,
    @SerializedName("bus_id") val busId: Int,
    @SerializedName("chofer_id") val choferId: Int
)