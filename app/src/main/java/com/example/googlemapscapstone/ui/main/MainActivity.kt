package com.example.googlemapscapstone.ui.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.googlemapscapstone.utils.LocationPermissionHelper
import com.example.googlemapscapstone.utils.geocodeLocation
import com.example.googlemapscapstone.utils.showRationaleDialog
import com.example.googlemapscapstone.utils.showSettingsAlertDialog
import com.example.googlemapscapstone.utils.showToast
import com.example.googlemapscapstone.viewmodel.GoogleMapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

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

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val searchedLocation = remember { mutableStateOf<LatLng?>(null) }
            val isSearchBarEnabled = remember { mutableStateOf(true) }
            val isRouteButtonEnabled = remember { mutableStateOf(true) }
            val isRouteSheetVisible = remember { mutableStateOf(false) }

            SearchBar(onSearch = { locationName ->
                geocodeLocation(this, locationName) { location ->
                    searchedLocation.value = location
                }
            },
                enabled = isSearchBarEnabled.value
            )

            GoogleMaps(
                state = viewModel.state.value,
                searchedLocation = searchedLocation.value,
                onGetCurrentLocation = {
                    if (LocationPermissionHelper.checkPermissions(this)) {
                        LocationPermissionHelper.getLocation(viewModel, fusedLocationProviderClient)
                    } else {
                        if (LocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                            showRationaleDialog(this) {
                                LocationPermissionHelper.requestPermissions(requestPermissionLauncher)
                            }
                        } else {
                            LocationPermissionHelper.requestPermissions(requestPermissionLauncher)
                        }
                    }
                },

                onRouteButtonClicked = {
                    isRouteButtonEnabled.value = false
                    isSearchBarEnabled.value = false
                    isRouteSheetVisible.value = true
                    Log.d("MainActivity", "Route button clicked. Search bar enabled: ${isSearchBarEnabled.value}, Route button enabled: ${isRouteButtonEnabled.value}")
                },
                isRouteButtonEnabled = isRouteButtonEnabled.value
            )

            /*
            This is used to control the visibility of the sheet
            to ensure that the state is updated immediately and the UI re-renders accordingly
             */
            if (isRouteSheetVisible.value) {
                ModalBottomSheet(
                    onDismissRequest = {
                        isRouteSheetVisible.value = false
                        isSearchBarEnabled.value = true
                        isRouteButtonEnabled.value = true
                    }
                ) {
                    SearchAndModeSelectorUI(onClose = {
                        isRouteSheetVisible.value = false
                        isSearchBarEnabled.value = true
                        isRouteButtonEnabled.value = true
                    })
                }
            }
        }
    }
}