package com.example.googlemapscapstone.ui.main.components.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyLocationButton(
    setCurrentLocationClicked: (Boolean) -> Unit,
    onGetCurrentLocation: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                setCurrentLocationClicked(true)
                onGetCurrentLocation()
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
                .offset(y = (-155).dp)
                .offset(x = (12).dp)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "My Location"
            )
        }
    }
}