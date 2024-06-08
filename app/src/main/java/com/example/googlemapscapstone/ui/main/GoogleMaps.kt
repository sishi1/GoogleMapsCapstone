package com.example.googlemapscapstone.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.googlemapscapstone.data.MapState
import com.example.googlemapscapstone.ui.main.components.GoogleMapsComponents
import com.example.googlemapscapstone.ui.main.components.HandleCurrentLocation
import com.example.googlemapscapstone.ui.main.components.HandleLocationUpdates
import com.example.googlemapscapstone.ui.main.components.MapTypeSelector
import com.example.googlemapscapstone.ui.main.components.MyLocationButton
import com.example.googlemapscapstone.ui.main.components.NavigationButton
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun GoogleMaps(
    state: MapState,
    searchedLocation: LatLng?,
    onGetCurrentLocation: () -> Unit,
    onMarkerPlaced: (LatLng?) -> Unit,
    onNavigationButtonClicked: () -> Unit,
    isRouteButtonEnabled: Boolean,
    polylinePoints: List<LatLng>,
    onPolylinePointsUpdated: (List<LatLng>) -> Unit
) {
    var mapTypeToRemember by remember { mutableStateOf(MapType.TERRAIN) }
    val markerState = rememberMarkerState(position = LatLng(0.0, 0.0))
    var isMarkerVisible by remember { mutableStateOf(false) }
    var currentLocationClicked by remember { mutableStateOf(false) }

    val mapProperties = MapProperties(
        isMyLocationEnabled = state.lastKnownLocation != null,
        mapType = mapTypeToRemember
    )

    val cameraPositionState = rememberCameraPositionState()
    val animationDuration = 2000

    HandleLocationUpdates(
        searchedLocation = searchedLocation,
        markerState = markerState,
        setIsMarkerVisible = { isMarkerVisible = it },
        cameraPositionState = cameraPositionState,
        animationDuration = animationDuration
    )

    HandleCurrentLocation(
        state = state,
        currentLocationClicked = currentLocationClicked,
        setCurrentLocationClicked = { currentLocationClicked = it },
        cameraPositionState = cameraPositionState,
        animationDuration = animationDuration
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMapsComponents(
            mapProperties = mapProperties,
            cameraPositionState = cameraPositionState,
            markerState = markerState,
            isMarkerVisible = isMarkerVisible,
            polylinePoints = polylinePoints,
            onPolylinePointsUpdated = onPolylinePointsUpdated,
            onMarkerPlaced = { latLng ->
                if (isMarkerVisible) {
                    isMarkerVisible = false
                    onMarkerPlaced(null)
                } else {
                    markerState.position = latLng
                    isMarkerVisible = true
                    onMarkerPlaced(latLng)
                }
            }
        )

        MyLocationButton(
            setCurrentLocationClicked = { currentLocationClicked = it },
            onGetCurrentLocation = onGetCurrentLocation
        )

        NavigationButton(
            isRouteButtonEnabled = isRouteButtonEnabled,
            onNavigationButtonClicked = onNavigationButtonClicked
        )
    }

    MapTypeSelector(
        onMapTypeChange = { mapTypeToRemember = it }
    )
}