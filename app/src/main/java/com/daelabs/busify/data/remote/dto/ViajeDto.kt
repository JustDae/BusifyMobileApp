package com.daelabs.busify.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.daelabs.busify.domain.model.Viaje
import com.daelabs.busify.domain.model.ViajePasajero
import com.daelabs.busify.domain.model.ViajeStatus

data class ViajePasajeroDto(
    val id: Int,
    @SerializedName("parada_id") val paradaId: Int,
    @SerializedName("parada_nombre") val paradaNombre: String,
    @SerializedName("pasajeros_subieron") val pasajerosSubieron: Int,
    @SerializedName("pasajeros_bajaron") val pasajerosBajaron: Int,
    @SerializedName("hora_paso") val horaPaso: String,
)

data class ViajeDto(
    @SerializedName(value = "id", alternate = ["pk", "viaje_id", "id_viaje", "ticket_id", "id_ticket", "ticket_no", "nro_ticket", "id_registro", "id_venta"]) val id: JsonElement?,
    @SerializedName("bus") val bus: JsonElement?,
    @SerializedName(value = "bus_plate", alternate = ["bus_placa", "plate", "placa", "placa_bus", "unidad_placa"]) val busPlaca: JsonElement?,
    @SerializedName(value = "bus_unit", alternate = ["bus_numero", "unit_number", "numero_bus", "unidad", "numero", "nro_unidad", "nro_bus"]) val busNumero: JsonElement?,
    @SerializedName(value = "route", alternate = ["ruta", "id_ruta", "recorrido"]) val route: JsonElement?,
    @SerializedName(value = "route_name", alternate = ["ruta_name", "nombre_ruta", "name_route", "nombre"]) val rutaName: JsonElement?,
    val status: JsonElement?,
    @SerializedName(value = "tarifa_total_recaudada", alternate = ["total_paid", "recaudado", "total_recaudado", "total_pagado", "paid_amount", "amount_paid", "monto_total", "valor_total", "pago_total", "price_total", "total_revenue", "revenue", "monto_total_pagado", "total_pago"]) val tarifaTotalRecaudada: JsonElement?,
    @SerializedName(value = "num_pasajeros_total", alternate = ["passenger_count", "num_pasajeros", "pasajeros_count", "total_passengers", "total_pasajeros", "cant_pasajeros", "passengers", "count", "tickets", "boletos", "ocupados", "asientos_ocupados", "num_tickets", "tickets_count", "total_tickets", "sold_tickets", "ventas", "quantity", "cantidad", "n_pasajeros", "cant", "num_pasajeros_subieron", "total_pasajeros_total", "pasajeros_actuales", "subieron", "pax_count", "pax_total", "total_pax", "current_pax", "ocupacion", "pax", "boletos_vendidos", "num_boletos", "cantidad_pasajeros", "qty", "numero_pasajeros"]) val numPasajerosTotal: JsonElement?,
    @SerializedName(value = "pasajeros", alternate = ["registros", "details", "items", "pasajero_detalle", "lista_pasajeros", "venta_detalle"]) val pasajeros: JsonElement?,
    @SerializedName(value = "tarifa", alternate = ["price", "fare", "costo", "valor", "base_fare", "tarifa_individual", "precio_unitario", "precio", "pago", "monto"]) val tarifaIndividual: JsonElement?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class CreateViajeRequestDto(
    @SerializedName("bus") val busId: Int,
    @SerializedName("route") val rutaId: Int,
)

data class AddRegistroRequestDto(
    @SerializedName("parada") val paradaId: Int,
    @SerializedName("pasajeros_subieron") val pasajerosSubieron: Int,
    @SerializedName("pasajeros_bajaron") val pasajerosBajaron: Int,
)

data class UpdateStatusRequestDto(
    val status: String,
)

data class ViajeStatsDto(
    @SerializedName("total_viajes") val totalViajes: Int?,
    @SerializedName("by_status") val byStatus: Map<String, Int>?,
)

fun ViajeDto.toDomain(): Viaje {
    fun JsonElement?.safeString(default: String = ""): String = try {
        if (this == null || isJsonNull) default
        else if (isJsonPrimitive) asString
        else toString()
    } catch (e: Exception) { default }

    fun JsonElement?.safeInt(default: Int = 0): Int = try {
        if (this == null || isJsonNull) default
        else if (isJsonPrimitive) {
            if (asJsonPrimitive.isNumber) asInt
            else asString.replace(Regex("[^0-9]"), "").toIntOrNull() ?: default
        } else default
    } catch (e: Exception) { default }

    fun JsonElement?.safeDouble(default: Double = 0.0): Double = try {
        if (this == null || isJsonNull) default
        else if (isJsonPrimitive) {
            if (asJsonPrimitive.isNumber) asDouble
            else asString.replace(",", ".").replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: default
        } else default
    } catch (e: Exception) { default }

    val realId = id.safeInt(0)

    val busIdStr = when {
        bus?.isJsonPrimitive == true -> bus.asString
        bus?.isJsonObject == true -> bus.asJsonObject.get("id").safeString()
        else -> null
    }

    val plate = if (bus?.isJsonObject == true) {
        val bObj = bus.asJsonObject
        bObj.get("plate").safeString().takeIf { it.isNotBlank() }
            ?: bObj.get("bus_plate").safeString().takeIf { it.isNotBlank() }
            ?: bObj.get("placa").safeString().takeIf { it.isNotBlank() }
            ?: bObj.get("unit_number").safeString().takeIf { it.isNotBlank() }
            ?: bObj.get("numero").safeString()
    } else {
        busPlaca.safeString()
    }

    val unit = if (bus?.isJsonObject == true) {
        val bObj = bus.asJsonObject
        bObj.get("unit_number").safeString().takeIf { it.isNotBlank() }
            ?: bObj.get("numero_bus").safeString().takeIf { it.isNotBlank() }
            ?: bObj.get("unidad").safeString().takeIf { it.isNotBlank() }
            ?: bObj.get("numero").safeString()
    } else {
        busNumero.safeString()
    }

    val routeObj = if (route?.isJsonObject == true) route.asJsonObject else null
    val routeIdStr = if (route?.isJsonPrimitive == true) route.asString else routeObj?.get("id").safeString()
    
    val rName = (routeObj?.get("name").safeString().takeIf { it.isNotBlank() }
        ?: routeObj?.get("nombre").safeString().takeIf { it.isNotBlank() }
        ?: rutaName.safeString().takeIf { it.isNotBlank() }
        ?: routeIdStr.takeIf { it.isNotBlank() }?.let { "Ruta #$it" }
        ?: "Ruta no asignada")

    val rTarifa = (routeObj?.get("tarifa").safeDouble()
        .takeIf { it > 0 }
        ?: routeObj?.get("price").safeDouble()
        .takeIf { it > 0 }
        ?: routeObj?.get("base_fare").safeDouble()
        .takeIf { it > 0 }
        ?: routeObj?.get("costo").safeDouble()
        .takeIf { it > 0 }
        ?: tarifaIndividual.safeDouble(0.35))

    var sumPasajeros = 0
    var sumRecaudado = 0.0
    val listaPasajeros = mutableListOf<ViajePasajero>()

    if (pasajeros?.isJsonArray == true) {
        pasajeros.asJsonArray.forEach { element ->
            if (element.isJsonObject) {
                val obj = element.asJsonObject
                val pSubieron = obj.get("pasajeros_subieron").safeInt(
                    obj.get("cantidad").safeInt(obj.get("num_pasajeros").safeInt(1))
                )
                val pTarifa = obj.get("tarifa_pagada").safeDouble(
                    obj.get("monto").safeDouble(obj.get("total").safeDouble(rTarifa))
                )
                sumPasajeros += pSubieron
                sumRecaudado += (pTarifa * pSubieron)
                listaPasajeros.add(ViajePasajero(
                    id = obj.get("id").safeInt(0),
                    pasajeroId = obj.get("id").safeInt(0),
                    pasajeroNombre = obj.get("parada_nombre").safeString().takeIf { it.isNotBlank() } ?: "Pasajero",
                    subioEnParadaId = obj.get("parada_id").safeInt(0),
                    tarifaPagada = pTarifa,
                    fechaRegistro = obj.get("hora_paso").safeString().takeIf { it.isNotBlank() } ?: obj.get("fecha").safeString()
                ))
            }
        }
    } else if (pasajeros?.isJsonPrimitive == true) {
        sumPasajeros = pasajeros.safeInt(0)
    }

    var nPasajeros = numPasajerosTotal.safeInt(0)
    var nRecaudado = tarifaTotalRecaudada.safeDouble(0.0)

    if (nPasajeros <= 0 && sumPasajeros > 0) nPasajeros = sumPasajeros
    if (nRecaudado <= 0.0 && sumRecaudado > 0.0) nRecaudado = sumRecaudado
    if (nRecaudado <= 0.0) nRecaudado = tarifaIndividual.safeDouble(0.0)
    if (sumPasajeros > nPasajeros) nPasajeros = sumPasajeros

    val finalTarifa = if (rTarifa > 0) rTarifa else 0.35
    if (nPasajeros > 0 && nRecaudado <= 0.0) nRecaudado = nPasajeros * finalTarifa
    if (nRecaudado > 0.0 && nPasajeros <= 0) nPasajeros = (nRecaudado / finalTarifa).toInt().coerceAtLeast(1)

    if (nPasajeros <= 0) nPasajeros = 1
    if (nRecaudado <= 0.0) nRecaudado = nPasajeros * finalTarifa

    return Viaje(
        id = realId,
        busId = busIdStr?.toIntOrNull(),
        busPlaca = plate.takeIf { it.isNotBlank() } ?: unit.takeIf { it.isNotBlank() } ?: busIdStr?.let { "ID: $it" } ?: "Sin Info",
        rutaId = routeIdStr?.toIntOrNull(),
        rutaNombre = rName,
        status = ViajeStatus.fromValue(status.safeString("pending")),
        tarifaTotalRecaudada = nRecaudado,
        numPasajerosTotal = nPasajeros,
        pasajeros = listaPasajeros,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: ""
    )
}
