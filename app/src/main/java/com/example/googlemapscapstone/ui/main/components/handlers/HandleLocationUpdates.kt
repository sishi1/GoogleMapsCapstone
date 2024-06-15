package com.example.googlemapscapstone.ui.main.components.handlers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

@Composable
fun HandleLocationUpdates(
    searchedLocation: LatLng?,
    markerState: MarkerState,
    setIsMarkerVisible: (Boolean) -> Unit,
    cameraPositionState: CameraPositionState,
    animationDuration: Int
) {
    LaunchedEffect(searchedLocation) {
        searchedLocation?.let {
            markerState.position = it
            setIsMarkerVisible(true)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it, 15f)
            cameraPositionState.animate(cameraUpdate, animationDuration)
        }
    }
}