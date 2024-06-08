package com.example.googlemapscapstone.ui.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.googlemapscapstone.ui.main.navigation.NavigationUIContainer
import com.example.googlemapscapstone.ui.main.navigation.TransportationMode
import com.example.googlemapscapstone.utils.LocationPermissionHelper
import com.example.googlemapscapstone.utils.geocodeLocation
import com.example.googlemapscapstone.utils.showRationaleDialog
import com.example.googlemapscapstone.utils.showSettingsAlertDialog
import com.example.googlemapscapstone.utils.showToast
import com.example.googlemapscapstone.viewmodel.GoogleMapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {

    private val permissionMessage = "Permission is required to use this feature."

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: GoogleMapsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionIsGranted: Boolean ->
        if (permissionIsGranted) {
            LocationPermissionHelper.getLocation(viewModel, fusedLocationProviderClient)
        } else {
            if (!LocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                showSettingsAlertDialog(this)
            } else {
                showToast(this, permissionMessage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val searchedLocation = remember { mutableStateOf<LatLng?>(null) }
            val isSearchBarEnabled = remember { mutableStateOf(true) }
            val isRouteButtonEnabled = remember { mutableStateOf(true) }
            val isNavigationUIVisible = remember { mutableStateOf(false) }
            val myLocation = remember { mutableStateOf("") }
            val destination = remember { mutableStateOf("") }
            val selectedMode = remember { mutableStateOf(TransportationMode.DRIVE) }
            val markerLocation = remember { mutableStateOf<LatLng?>(null) }
            val polylinePoints = remember { mutableStateOf(listOf<LatLng>()) }

            GoogleMaps(
                state = viewModel.state.value,
                searchedLocation = searchedLocation.value,
                onGetCurrentLocation = {
                    if (LocationPermissionHelper.checkPermissions(this)) {
                        LocationPermissionHelper.getLocation(viewModel, fusedLocationProviderClient)
                    } else {
                        if (LocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                            showRationaleDialog(this) {
                                LocationPermissionHelper.requestPermissions(
                                    requestPermissionLauncher
                                )
                            }
                        } else {
                            LocationPermissionHelper.requestPermissions(requestPermissionLauncher)
                        }
                    }
                },
                onMarkerPlaced = { location ->
                    markerLocation.value = location
                },
                onNavigationButtonClicked = {
                    val currentLocation = viewModel.state.value.lastKnownLocation
                    if (currentLocation != null) {
                        myLocation.value =
                            "${currentLocation.latitude},${currentLocation.longitude}"
                    }
                    destination.value = when {
                        searchedLocation.value != null -> "${searchedLocation.value!!.latitude},${searchedLocation.value!!.longitude}"
                        markerLocation.value != null -> "${markerLocation.value!!.latitude},${markerLocation.value!!.longitude}"
                        else -> ""
                    }
                    isRouteButtonEnabled.value = false
                    isSearchBarEnabled.value = false
                    isNavigationUIVisible.value = true
                },
                isRouteButtonEnabled = isRouteButtonEnabled.value,
                polylinePoints = polylinePoints.value,
                onPolylinePointsUpdated = { points ->
                    polylinePoints.value = points
                }
            )

            SearchBar(
                onSearch = { locationName ->
                    geocodeLocation(this, locationName) { location ->
                        searchedLocation.value = location
                    }
                },
                enabled = isSearchBarEnabled.value
            )

            if (isNavigationUIVisible.value) {
                NavigationUIContainer(
                    context =  this,
                    isNavigationUIVisible = isNavigationUIVisible,
                    isSearchBarEnabled = isSearchBarEnabled,
                    isRouteButtonEnabled = isRouteButtonEnabled,
                    myLocation = myLocation,
                    destination = destination,
                    selectedMode = selectedMode,
                    searchedLocation = searchedLocation,
                    markerLocation = markerLocation,
                    polylinePoints = polylinePoints
                )
            }
        }
    }
}