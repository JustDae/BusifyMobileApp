package com.daelabs.busify

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daelabs.busify.theme.Accent
import com.daelabs.busify.theme.Background
import com.daelabs.busify.theme.Border
import com.daelabs.busify.theme.BorderLight
import com.daelabs.busify.theme.Error
import com.daelabs.busify.theme.Info
import com.daelabs.busify.theme.BusifyTheme
import com.daelabs.busify.theme.Success
import com.daelabs.busify.theme.Surface
import com.daelabs.busify.theme.TextFaint
import com.daelabs.busify.theme.TextPrimary
import com.daelabs.busify.theme.TextSecondary
import com.daelabs.busify.theme.Warning

@Composable
fun VerificationScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Busify",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Accent,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "Android + Jetpack Compose",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            EnvCard(
                items = listOf(
                    "Kotlin" to "2.0.21",
                    "Compose BOM" to "2024.10.01",
                    "Material 3" to "✓",
                    "Hilt" to "2.52",
                    "Retrofit" to "2.11.0",
                    "API URL" to BuildConfig.API_BASE_URL,
                ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Design System",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.Start),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(
                    "Accent" to Accent,
                    "Success" to Success,
                    "Warning" to Warning,
                    "Error" to Error,
                    "Info" to Info,
                ).forEach { (label, color) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(color, RoundedCornerShape(8.dp)),
                        )
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            color = TextFaint,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Modelos de dominio",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.Start),
            )

            listOf("Auth.kt", "Bus.kt", "Chofer.kt", "Parada.kt", "Ruta.kt", "User.kt", "Viaje.kt").forEach { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "domain/model/$file",
                        style = MaterialTheme.typography.bodySmall,
                        color = Accent,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "✓",
                        color = Success,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun EnvCard(items: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(
            text = "Estado del entorno",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.first,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Text(
                    text = item.second,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
            }

            if (index < items.lastIndex) {
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview() {
    BusifyTheme {
        VerificationScreen()
    }
}