package com.example.googlemapscapstone.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.googlemapscapstone.data.MapState
import com.example.googlemapscapstone.R
import com.example.googlemapscapstone.api.drawRoute
import com.example.googlemapscapstone.ui.main.components.SheetContent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMaps(
    state: MapState,
    searchedLocation: LatLng?,
    onGetCurrentLocation: () -> Unit,
    onRouteButtonClicked: () -> Unit,
    isRouteButtonEnabled: Boolean
) {
    var mapTypeToRemember by remember { mutableStateOf(MapType.TERRAIN) }
    val markerState = rememberMarkerState(position = LatLng(0.0, 0.0))
    var isMarkerVisible by remember { mutableStateOf(false) }
    var currentLocationClicked by remember { mutableStateOf(false) }
    var markedLocation by remember { mutableStateOf<LatLng?>(null) }
    var isRouteButtonVisible by remember { mutableStateOf(false) }
    var polylinePoints by remember { mutableStateOf(listOf<LatLng>()) }

    val context = LocalContext.current

    val mapProperties = MapProperties(
        isMyLocationEnabled = state.lastKnownLocation != null,
        mapType = mapTypeToRemember
    )

    val cameraPositionState = rememberCameraPositionState()
    val animationDuration = 2000

    LaunchedEffect(searchedLocation) {
        searchedLocation?.let {
            markerState.position = it
            isMarkerVisible = true
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it, 15f)
            cameraPositionState.animate(cameraUpdate, animationDuration)
        }
    }

    state.lastKnownLocation?.let { location ->
        val latLng = LatLng(location.latitude, location.longitude)
        if (currentLocationClicked) {
            LaunchedEffect(latLng) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                cameraPositionState.animate(cameraUpdate, 2000)

                currentLocationClicked = false // Reset the flag after animating
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                if (isMarkerVisible) {
                    isMarkerVisible = false
                    isRouteButtonVisible = false
                    polylinePoints = emptyList()
                } else {
                    markerState.position = latLng
                    isMarkerVisible = true
                    isRouteButtonVisible = true
                }
            }
        ) {
            if (isMarkerVisible) {
                Marker(
                    state = markerState,
                    title = "Marker",
                    snippet = "Lat: ${markerState.position.latitude}, Lng: ${markerState.position.longitude}"
                )
                markedLocation = LatLng(markerState.position.latitude, markerState.position.longitude)
            }

            if (polylinePoints.isNotEmpty()) {
                Polyline(
                    points = polylinePoints,
                    color = Color.Red,
                    width = 10f
                )
            }
        }

        FloatingActionButton(
            onClick = {
                isMarkerVisible = false
                currentLocationClicked = true
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

        if (isRouteButtonVisible) {
            FloatingActionButton(
                onClick = {
                    drawRoute(
                        context,
                        currentLocation = state.lastKnownLocation?.let {
                            LatLng(it.latitude, it.longitude)
                        },
                        searchedLocation = searchedLocation,
                        markedLocation = markedLocation,
                        travelMode = "driving", // Needs to be dynamic
                        departureTime = "now", // Needs to be dynamic
                        onRouteDrawn = { points, routeInfo ->
                            polylinePoints = points
                            // Optionally, handle routeInfo
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = "Route button",
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    if (isRouteButtonEnabled) {
                        onRouteButtonClicked()
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
                    .offset(y = (-75).dp)
                    .offset(x = (27).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = "Navigation Button"
                )
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        FloatingActionButton(
            onClick =
            { isSheetOpen = true },
            Modifier
                .size(40.dp)
                .align(Alignment.End)
                .offset(y = (70).dp)
                .offset(x = (-5).dp)
                .background(Color.Transparent),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.stack_icon),
                contentDescription = "Map Type Button",
            )
        }
        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { isSheetOpen = false },
                dragHandle = null
            ) {
                SheetContent(onMapTypeChange = { mapType ->
                    mapTypeToRemember = mapType
                    isSheetOpen = false
                })
            }
        }
    }
}

@Composable
fun SearchAndModeSelectorUI(onClose: () -> Unit) {
    var selectedMode by remember { mutableStateOf(TransportationMode.CAR) }
    var myLocation by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Fields
        LocationInputField(value = myLocation, onValueChange = { myLocation = it }, label = "My location", icon = Icons.Default.MyLocation)
        Spacer(modifier = Modifier.height(8.dp))
        LocationInputField(value = destination, onValueChange = { destination = it }, label = "Destination", icon = Icons.Default.AddLocation)
        Spacer(modifier = Modifier.height(16.dp))

        // Transportation Mode Selector
        TransportationModeSelector(selectedMode) { mode ->
            selectedMode = mode
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Close Button
        Button(onClick = onClose) {
            Text("Close")
        }
    }
}

@Composable
fun LocationInputField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, fontSize = 16.sp) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.Gray
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun TransportationModeSelector(selectedMode: TransportationMode, onModeSelected: (TransportationMode) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransportationModeButton(
            icon = Icons.Default.DirectionsCar,
            isSelected = selectedMode == TransportationMode.CAR,
            onClick = { onModeSelected(TransportationMode.CAR) }
        )
        TransportationModeButton(
            icon = Icons.Default.DirectionsBus,
            isSelected = selectedMode == TransportationMode.BUS,
            onClick = { onModeSelected(TransportationMode.BUS) }
        )
        TransportationModeButton(
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            isSelected = selectedMode == TransportationMode.WALK,
            onClick = { onModeSelected(TransportationMode.WALK) }
        )
        TransportationModeButton(
            icon = Icons.AutoMirrored.Filled.DirectionsBike,
            isSelected = selectedMode == TransportationMode.BIKE,
            onClick = { onModeSelected(TransportationMode.BIKE) }
        )
    }
}

@Composable
fun TransportationModeButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
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

enum class TransportationMode {
    CAR, BUS, WALK, BIKE
}
