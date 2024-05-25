package com.example.googlemapscapstone

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun GoogleMaps(
    state: MapState,
    searchedLocation: LatLng?, // Add a parameter for the searched location
) {
    val markerState = rememberMarkerState(position = LatLng(0.0, 0.0))
    var isMarkerVisible by remember { mutableStateOf(false) }

    val mapProperties =
        MapProperties(
            isMyLocationEnabled = state.lastKnownLocation != null
        )

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(searchedLocation) {
        searchedLocation?.let {
            markerState.position = it
            isMarkerVisible = true
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it, 15f)
            val animationDuration = 2000
            cameraPositionState.animate(cameraUpdate, animationDuration)
        }
    }

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
