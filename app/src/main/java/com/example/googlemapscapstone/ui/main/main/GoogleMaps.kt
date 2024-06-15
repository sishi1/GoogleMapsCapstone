package com.example.googlemapscapstone.ui.main.main

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.googlemapscapstone.api.fetchReverseGeocode
import com.example.googlemapscapstone.data.MapState
import com.example.googlemapscapstone.ui.main.components.maps.GoogleMapsComponents
import com.example.googlemapscapstone.ui.main.components.handlers.HandleCurrentLocation
import com.example.googlemapscapstone.ui.main.components.handlers.HandleLocationUpdates
import com.example.googlemapscapstone.ui.main.components.handlers.HandleMarkerLocationUpdates
import com.example.googlemapscapstone.ui.main.components.maps.MapTypeSelector
import com.example.googlemapscapstone.ui.main.components.maps.MyLocationButton
import com.example.googlemapscapstone.ui.main.components.maps.NavigationButton
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun GoogleMaps(
    context: Context,
    state: MapState,
    searchedLocation: MutableState<LatLng?>,
    markerLocation: MutableState<LatLng?>,
    isNavigationUIVisible: MutableState<Boolean>,
    onGetCurrentLocation: () -> Unit,
    onMarkerPlaced: (LatLng?) -> Unit,
    polylinePoints: List<LatLng>,
    onPolylinePointsUpdated: (List<LatLng>) -> Unit,
    onClearDestination: () -> Unit,
    onUpdateDestination: (String) -> Unit,
    onUpdateMyLocation: (String) -> Unit
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

    HandleMarkerLocationUpdates(
        context = context,
        markerLocation = markerLocation.value,
        markerState = markerState,
        setIsMarkerVisible = { isMarkerVisible = it },
        cameraPositionState = cameraPositionState,
        animationDuration = animationDuration,
        onUpdateDestination = onUpdateDestination,
    )

    HandleLocationUpdates(
        searchedLocation = searchedLocation.value,
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
        animationDuration = animationDuration,
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
                    onClearDestination()
                } else {
                    markerState.position = latLng
                    isMarkerVisible = true
                    onMarkerPlaced(latLng)
                    fetchReverseGeocode(context, latLng) { latLng, address ->
                        if (latLng != null) {
                            onUpdateDestination(address ?: "${latLng.latitude}, ${latLng.longitude}")
                        }
                    }
                }
            }
        )

        MyLocationButton(
            setCurrentLocationClicked = { currentLocationClicked = it },
            onGetCurrentLocation = {
                onGetCurrentLocation()
                val currentLocation = state.lastKnownLocation
                if (currentLocation != null) {
                    val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    fetchReverseGeocode(context, latLng) { _, address ->
                        if (address != null) {
                            onUpdateMyLocation(address)
                        } else {
                            onUpdateMyLocation("${latLng.latitude},${latLng.longitude}")
                        }
                    }
                }
            }
        )

        NavigationButton(
            onNavigationButtonClicked = {
                isNavigationUIVisible.value = true
            }
        )
    }

    MapTypeSelector(
        onMapTypeChange = { mapTypeToRemember = it }
    )
}
