package com.example.googlemapscapstone.ui.main.main

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
            val searchedLocationName = remember { mutableStateOf<String?>(null) }
            val isSearchBarEnabled = remember { mutableStateOf(true) }
            val isRouteButtonEnabled = remember { mutableStateOf(true) }
            val isNavigationUIVisible = remember { mutableStateOf(false) }
            val myLocation = remember { mutableStateOf("") }
            val myLatLng = remember { mutableStateOf<LatLng?>(null) }
            val destination = remember { mutableStateOf("") }
            val selectedMode = remember { mutableStateOf(TransportationMode.DRIVE) }
            val markerLocation = remember { mutableStateOf<LatLng?>(null) }
            val polylinePoints = remember { mutableStateOf(listOf<LatLng>()) }

            GoogleMaps(
                context = this,
                state = viewModel.state.value,
                searchedLocation = searchedLocation,
                markerLocation = markerLocation,
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
                isNavigationUIVisible = isNavigationUIVisible,
                polylinePoints = polylinePoints.value,
                onPolylinePointsUpdated = { points ->
                    polylinePoints.value = points
                },
                onClearDestination = {
                    destination.value = ""
                },
                onUpdateDestination = { address ->
                    destination.value = address
                },
                onUpdateMyLocation = { myLocation.value = it }
            )

            SearchBar(
                context = this,
                onSearch = { location, address ->
                    searchedLocation.value = location
                    searchedLocationName.value = address
                    destination.value = address ?: ""
                },
                enabled = isSearchBarEnabled.value
            )

            if (isNavigationUIVisible.value) {
                NavigationUIContainer(
                    context = this,
                    isNavigationUIVisible = isNavigationUIVisible,
                    isSearchBarEnabled = isSearchBarEnabled,
                    isRouteButtonEnabled = isRouteButtonEnabled,
                    myLocation = myLocation,
                    myLatLng = myLatLng,
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