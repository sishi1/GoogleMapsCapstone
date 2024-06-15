package com.example.googlemapscapstone.ui.main.components.handlers

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.googlemapscapstone.api.fetchReverseGeocode
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MarkerState

@Composable
fun HandleMarkerLocationUpdates(
    context: Context,
    markerLocation: LatLng?,
    markerState: MarkerState,
    setIsMarkerVisible: (Boolean) -> Unit,
    cameraPositionState: CameraPositionState,
    animationDuration: Int,
    onUpdateDestination: (String) -> Unit
) {
    LaunchedEffect(markerLocation) {
        markerLocation?.let { location ->
            markerState.position = location
            setIsMarkerVisible(true)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(location, 10f),
                animationDuration
            )

            fetchReverseGeocode(context, location) { latLng, address ->
                onUpdateDestination(address ?: "${location.latitude}, ${location.longitude}")
            }
        }
    }
}
