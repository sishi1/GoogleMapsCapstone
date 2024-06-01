package com.example.googlemapscapstone

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: GoogleMapsViewModel by viewModels()
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionIsGranted: Boolean ->
        if (permissionIsGranted) {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
        } else {
            Log.d("Permission", "Permission denied")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionHelper = LocationPermissionHelper(this)

        if (locationPermissionHelper.checkPermissions()) {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
        } else {
            locationPermissionHelper.requestPermissions(requestPermissionLauncher)
        }

        setContent {
            val searchedLocation = remember { mutableStateOf<LatLng?>(null) }

            GoogleMaps(
                state = viewModel.state.value,
                searchedLocation = searchedLocation.value,
                onGetCurrentLocation = {
                    if (locationPermissionHelper.checkPermissions()) {
                        viewModel.getDeviceLocation(fusedLocationProviderClient)
                    } else {
                        locationPermissionHelper.requestPermissions(requestPermissionLauncher)
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