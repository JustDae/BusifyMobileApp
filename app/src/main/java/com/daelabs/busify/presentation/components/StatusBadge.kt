package com.daelabs.busify.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
    ViajeStatus.PENDING    -> Color(0xFF3498DB)
    ViajeStatus.SCHEDULED  -> Color(0xFF9B59B6)
    ViajeStatus.ON_ROUTE   -> Color(0xFF2EBD6B)
    ViajeStatus.COMPLETED  -> Color(0xFF95A5A6)
    ViajeStatus.CANCELLED  -> Color(0xFFE74C3C)
}

@Composable
fun StatusBadge(status: ViajeStatus, modifier: Modifier = Modifier) {
    val color = viajeStatusColor(status)
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape),
            )
            Text(
                text = status.label.uppercase(),
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
