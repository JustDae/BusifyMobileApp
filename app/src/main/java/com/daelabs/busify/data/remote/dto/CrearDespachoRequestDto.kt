package com.daelabs.busify.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class CrearDespachoRequestDto(
    @SerializedName("ruta")
    val rutaId: Int,

    @SerializedName("passenger_count")
    val passengerCount: Int = 0,

    @SerializedName("status")
    val status: String = "scheduled",

    @SerializedName("departure_time")
    val departureTime: String = obtenerHoraActualIso(0),

    @SerializedName("estimated_arrival")
    val estimatedArrival: String = obtenerHoraActualIso(2)
)

private fun obtenerHoraActualIso(horasDeAñadido: Int): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val tiempoCalculado = System.currentTimeMillis() + (horasDeAñadido * 3600000L)
    return sdf.format(Date(tiempoCalculado))
}