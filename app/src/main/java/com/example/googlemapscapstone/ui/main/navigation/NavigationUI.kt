package com.example.googlemapscapstone.ui.main.navigation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.googlemapscapstone.api.drawRoute
import com.example.googlemapscapstone.api.handleGeocodeInput
import com.example.googlemapscapstone.ui.main.components.input.LocationInputField
import com.example.googlemapscapstone.utils.showAlertDialog
import com.google.android.gms.maps.model.LatLng

@Composable
fun NavigationUI(
    myLocation: String,
    destination: String,
    selectedMode: TransportationMode,
    onMyLocationChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onModeChange: (TransportationMode) -> Unit,
    onRouteClick: () -> Unit,
    onClose: () -> Unit,
    onSwitchLocations: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LocationInputField(
                value = myLocation,
                onValueChange = onMyLocationChange,
                label = "My location",
                icon = Icons.Default.MyLocation,
            )
            Spacer(modifier = Modifier.height(8.dp))

            LocationInputField(
                value = destination,
                onValueChange = onDestinationChange,
                label = "Destination",
                icon = Icons.Default.LocationOn,
            )
            Spacer(modifier = Modifier.height(16.dp))

            TransportationModeSelector(selectedMode) { mode ->
                onModeChange(mode)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onClose) {
                    Text("Close")
                }

                Button(onClick = onRouteClick) {
                    Text("Route")
                }
            }
        }

        IconButton(
            onClick = onSwitchLocations,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 16.dp, y = 64.dp)
        ) {
            Icon(
                Icons.Default.SwapVert,
                contentDescription = "Switch Locations"
            )
        }
    }
}



@Composable
fun NavigationUIContainer(
    context: Context,
    isNavigationUIVisible: MutableState<Boolean>,
    isRouteButtonEnabled: MutableState<Boolean>,
    myLocation: MutableState<String>,
    myLatLng: MutableState<LatLng?>,
    destination: MutableState<String>,
    selectedMode: MutableState<TransportationMode>,
    searchedLocation: MutableState<LatLng?>,
    markerLocation: MutableState<LatLng?>,
    polylinePoints: MutableState<List<LatLng>>,
    onSwitchLocations: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(315.dp)
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {}
    ) {
        NavigationUI(
            myLocation = myLocation.value,
            destination = destination.value,
            selectedMode = selectedMode.value,
            onMyLocationChange = { location ->
                myLocation.value = location
            },
            onDestinationChange = { destination.value = it },
            onModeChange = { selectedMode.value = it },
            onRouteClick = {
                handleGeocodeInput(context, myLocation.value) { latLng, _ ->
                    myLatLng.value = latLng

                    if (myLatLng.value == null || destination.value.isEmpty()) {
                        showAlertDialog(
                            context = context,
                            title = "Empty Locations",
                            message = "Current or destination location is empty. Please try again."
                        )
                    } else {
                        handleGeocodeInput(context, destination.value) { location, _ ->
                            if (location != null) {
                                markerLocation.value = location
                                drawRoute(
                                    context = context,
                                    currentLocation = myLatLng.value,
                                    searchedLocation = searchedLocation.value,
                                    markedLocation = markerLocation.value,
                                    travelMode = selectedMode.value.name.lowercase(),
                                    departureTime = "now",
                                    onRouteDrawn = { points ->
                                        polylinePoints.value = points
                                    }
                                )
                            } else {
                                showAlertDialog(
                                    context = context,
                                    title = "Navigation Error",
                                    message = "Failed to find the location for the destination: ${destination.value}"
                                )
                            }
                        }
                    }
                }
            },
            onClose = {
                isNavigationUIVisible.value = false
                isRouteButtonEnabled.value = true
                myLocation.value = ""
                polylinePoints.value = emptyList()
            },
            onSwitchLocations = onSwitchLocations,
        )
    }
}
