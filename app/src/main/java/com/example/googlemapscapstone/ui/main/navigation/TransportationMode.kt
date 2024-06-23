package com.example.googlemapscapstone.ui.main.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class TransportationMode {
    DRIVE, BICYCLING, WALKING, TRANSIT
}
@Composable
fun TransportationModeSelector(
    selectedMode: TransportationMode,
    onModeSelected: (TransportationMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransportationModeButton(
            icon = Icons.Default.DirectionsCar,
            isSelected = selectedMode == TransportationMode.DRIVE,
            onClick = { onModeSelected(TransportationMode.DRIVE) }
        )
        TransportationModeButton(
            icon = Icons.Default.DirectionsBus,
            isSelected = selectedMode == TransportationMode.TRANSIT,
            onClick = { onModeSelected(TransportationMode.TRANSIT) }
        )
        TransportationModeButton(
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            isSelected = selectedMode == TransportationMode.WALKING,
            onClick = { onModeSelected(TransportationMode.WALKING) }
        )
        TransportationModeButton(
            icon = Icons.AutoMirrored.Filled.DirectionsBike,
            isSelected = selectedMode == TransportationMode.BICYCLING,
            onClick = { onModeSelected(TransportationMode.BICYCLING) }
        )
    }
}

@Composable
fun TransportationModeButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (isSelected) Color.Gray else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Icon(icon, contentDescription = null)
    }
}