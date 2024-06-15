package com.example.googlemapscapstone.ui.main.components.handlers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.googlemapscapstone.data.MapState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

@Composable
fun HandleCurrentLocation(
    state: MapState,
    currentLocationClicked: Boolean,
    setCurrentLocationClicked: (Boolean) -> Unit,
    cameraPositionState: CameraPositionState,
    animationDuration: Int
) {
    state.lastKnownLocation?.let { location ->
        val latLng = LatLng(location.latitude, location.longitude)
        if (currentLocationClicked) {
            LaunchedEffect(latLng) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                cameraPositionState.animate(cameraUpdate, animationDuration)
                setCurrentLocationClicked(false)
            }
        }
    }
}