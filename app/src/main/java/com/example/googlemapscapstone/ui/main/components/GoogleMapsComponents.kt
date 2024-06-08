package com.example.googlemapscapstone.ui.main.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng

@Composable
fun GoogleMapsComponents(
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    markerState: MarkerState,
    isMarkerVisible: Boolean,
    polylinePoints: List<LatLng>,
    onPolylinePointsUpdated: (List<LatLng>) -> Unit,
    onMarkerPlaced: (LatLng) -> Unit
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = mapProperties,
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            if (isMarkerVisible) {
                onPolylinePointsUpdated(emptyList())
            }
            onMarkerPlaced(latLng)
        }
    ) {
        if (isMarkerVisible) {
            Marker(state = markerState)
        }

        if (polylinePoints.isNotEmpty()) {
            Polyline(
                points = polylinePoints,
                color = Color.Red,
                width = 10f
            )
        }
    }
}