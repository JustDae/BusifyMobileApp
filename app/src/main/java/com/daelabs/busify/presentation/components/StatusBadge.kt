package com.daelabs.busify.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daelabs.busify.domain.model.ViajeStatus

fun viajeStatusColor(status: ViajeStatus): Color = when (status) {
    ViajeStatus.PENDING    -> Color(0xFF3182CE)
    ViajeStatus.EN_PROGRESO -> Color(0xFFDD6B20)
    ViajeStatus.COMPLETADO -> Color(0xFF38A169)
    ViajeStatus.CANCELADO  -> Color(0xFFE53E3E)
}

@Composable
fun StatusBadge(status: ViajeStatus, modifier: Modifier = Modifier) {
    val color = viajeStatusColor(status)
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, RoundedCornerShape(50)),
        )
        Text(
            text = status.name,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
        )
    }
}