package com.example.weargoogle.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import androidx.compose.foundation.shape.CircleShape
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import com.example.weargoogle.R
import com.example.weargoogle.presentation.theme.WeargoogleTheme

@Composable
fun StatsScreen(
    humidity: Int,
    temperature: Int,
    pressure: Int,
    luminosity: Int,
    airQuality: Int,
    gardenState: GardenState
) {
    WeargoogleTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            TimeText(modifier = Modifier.align(Alignment.TopCenter))

            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { 
                    GardenStateChip(gardenState)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CircularStatDisplay(
                            value = temperature,
                            maxValue = 50,
                            unit = "°C",
                            icon = R.drawable.ic_temperature_foreground,
                            color = Color(0xFFFF5722)
                        )
                        CircularStatDisplay(
                            value = humidity,
                            maxValue = 100,
                            unit = "%",
                            icon = R.drawable.ic_humidity,
                            color = Color(0xFF03A9F4)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    CompactChipGroup(pressure, luminosity, airQuality)
                }
            }
        }
    }
}

@Composable
fun GardenStateChip(state: GardenState) {
    val (color, icon, text) = when (state) {
        GardenState.GOOD -> Triple(Color(0xFF4CAF50), "🌿", "Garden Status: Good")
        GardenState.WARNING -> Triple(Color(0xFFFFC107), "⚠️", "Check Garden Values")
        GardenState.BAD -> Triple(Color(0xFFFF5722), "❗", "Garden Needs Help!")
        GardenState.ERROR -> Triple(Color(0xFFF44336), "⚡", "System Error")
        GardenState.NONE -> Triple(Color.Gray, "🌱", "Initializing...")
    }

    TitleCard(
        onClick = { },
        title = { Text(text) },
        backgroundPainter = CardDefaults.cardBackgroundPainter(
            startBackgroundColor = color,
            endBackgroundColor = color.copy(alpha = 0.7f)
        ),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = icon,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CircularStatDisplay(
    value: Int,
    maxValue: Int,
    unit: String,
    icon: Int,
    color: Color
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(80.dp)
    ) {
        CircularProgressIndicator(
            progress = value.toFloat() / maxValue,
            modifier = Modifier.fillMaxSize(),
            startAngle = 315f,
            endAngle = 225f,
            strokeWidth = 4.dp,
            indicatorColor = color
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$value$unit",
                style = MaterialTheme.typography.title2,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun CompactChipGroup(pressure: Int, luminosity: Int, airQuality: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CompactChip(
            onClick = { },
            label = { Text("$pressure hPa") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_pressure),
                    contentDescription = null,
                    tint = Color(0xFF9C27B0)
                )
            }
        )
        
        CompactChip(
            onClick = { },
            label = { Text("$luminosity lx") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_luminosity),
                    contentDescription = null,
                    tint = Color(0xFFFFEB3B)
                )
            }
        )

        CompactChip(
            onClick = { },
            label = { Text("$airQuality AQI") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_air_quality),
                    contentDescription = null,
                    tint = Color(0xFF66BB6A)
                )
            }
        )
    }
}

