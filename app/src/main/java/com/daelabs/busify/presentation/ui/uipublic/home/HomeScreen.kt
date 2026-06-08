package com.daelabs.busify.presentation.ui.uipublic.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale
import coil.compose.AsyncImage
import com.daelabs.busify.domain.model.Ruta
import com.daelabs.busify.presentation.viewmodel.CatalogViewModel

@Composable
fun HomeScreen(
    onRutaClick: (Int) -> Unit,
    onCatalogClick: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val primaryGreen = Color(0xFF2EBD6B)
    val lightGreen = Color(0xFFE8F5E9)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFDFA)),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 56.dp),
            ) {
                val rotation = rememberInfiniteTransition(label = "refresh")
                    .animateFloat(
                        initialValue = 0f,
                        targetValue = if (state.isLoading) 360f else 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "rotation"
                    )

                IconButton(
                    onClick = viewModel::refresh,
                    modifier = Modifier.align(Alignment.TopEnd),
                    enabled = !state.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar",
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(26.dp)
                            .let { if (state.isLoading) it.rotate(rotation.value) else it }
                    )
                }

                Column {
                    Surface(
                        color = lightGreen,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Text(
                            text = "VIAJA SEGURO",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryGreen
                        )
                    }

                    Text(
                        text = "Tu Destino",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF424242),
                        lineHeight = 36.sp
                    )
                    Text(
                        text = "Busify",
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryGreen,
                        letterSpacing = (-1.5).sp,
                        lineHeight = 48.sp
                    )
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Text(
                        text = "Encuentra tu ruta ideal, compra tus pasajes y llega a tiempo a tu destino con monitoreo en tiempo real.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        lineHeight = 24.sp
                    )
                    
                    Spacer(Modifier.height(36.dp))
                    
                    Button(
                        onClick = onCatalogClick,
                        modifier = Modifier
                            .height(60.dp)
                            .widthIn(min = 200.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGreen,
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(18.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text("Buscar Boletos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        if (state.cooperativas.isNotEmpty()) {
            item {
                SectionHeader(title = "Cooperativas", onSeeAll = onCatalogClick)
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(state.cooperativas.take(6)) { coop ->
                        CooperativaChip(
                            name = coop.name,
                            count = coop.totalRutas,
                            onClick = { onCatalogClick() },
                            primaryGreen = primaryGreen
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }

        item {
            SectionHeader(title = "Líneas de Alto Flujo", onSeeAll = onCatalogClick)
        }

        if (state.isLoading) {
            item {
                Box(Modifier.fillMaxWidth().height(200.dp), Alignment.Center) {
                    CircularProgressIndicator(color = primaryGreen, strokeWidth = 3.dp)
                }
            }
        } else {
            val chunked = state.rutas.take(4).chunked(2)
            items(chunked) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    row.forEach { ruta ->
                        RutaCard(
                            ruta = ruta,
                            onClick = { onRutaClick(ruta.id) },
                            modifier = Modifier.weight(1f),
                            primaryGreen = primaryGreen
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1A1A1A),
        )
        TextButton(
            onClick = onSeeAll,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text("Ver todas", color = Color(0xFF2EBD6B), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CooperativaChip(name: String, count: Int, onClick: () -> Unit, primaryGreen: Color) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        modifier = Modifier.width(160.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Surface(
                color = primaryGreen.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = primaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$count líneas",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
            )
        }
    }
}

@Composable
fun RutaCard(
    ruta: Ruta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryGreen: Color = Color(0xFF2EBD6B)
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        modifier = modifier,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center,
            ) {
                if (ruta.mapaSnippetUrl != null) {
                    AsyncImage(
                        model = ruta.mapaSnippetUrl,
                        contentDescription = ruta.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        Icons.Default.DirectionsBus,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = Color.LightGray
                    )
                }

                if (!ruta.hasBusesActivos) {
                    Surface(
                        color = Color(0xFFFF5252).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "INACTIVA",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = ruta.cooperativaName?.uppercase() ?: "LÍNEA COMÚN",
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryGreen,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = ruta.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", ruta.tarifa)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = primaryGreen,
                    )

                    Surface(
                        color = Color(0xFFF1F5F2),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalActivity,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.Gray
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${ruta.totalBuses}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
