package com.example.googlemapscapstone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun GoogleMaps(
    state: MapState
) {
    var markerState = rememberMarkerState(position = LatLng(0.0, 0.0))
    var isMarkerVisible by remember { mutableStateOf(false) }

    var mapProperties =
        MapProperties(
            isMyLocationEnabled = state.lastKnownLocation != null
        )

    val cameraPositionState = rememberCameraPositionState()

    Box(modifier = Modifier.fillMaxSize())
    {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                if (isMarkerVisible) {
                    isMarkerVisible = false
                } else {
                    markerState.position = latLng
                    isMarkerVisible = true
                }
            }
        ) {
            if (isMarkerVisible) {
                Marker(
                    state = markerState,
                    title = "Marker",
                    snippet = "Lat: ${markerState.position.latitude}, Lng: ${markerState.position.longitude}"
                )
            }
        }
    }
}
