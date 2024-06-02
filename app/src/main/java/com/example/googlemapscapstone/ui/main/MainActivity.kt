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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val searchedLocation = remember { mutableStateOf<LatLng?>(null) }

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
                }
            )

            SearchBar(onSearch = { locationName ->
                geocodeLocation(this, locationName) { location ->
                    searchedLocation.value = location
                }
            })
        }
    }
}