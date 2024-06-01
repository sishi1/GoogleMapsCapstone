package com.example.googlemapscapstone.ui.main

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
import com.example.googlemapscapstone.utils.LocationPermissionHelper
import com.example.googlemapscapstone.utils.geocodeLocation
import com.example.googlemapscapstone.viewmodel.GoogleMapsViewModel
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
            getLocation()
        } else {
            Log.d("Permission", "Permission denied")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionHelper = LocationPermissionHelper(this)

        checkLocationPermission()

        setContent {
            val searchedLocation = remember { mutableStateOf<LatLng?>(null) }

            GoogleMaps(
                state = viewModel.state.value,
                searchedLocation = searchedLocation.value,
                onGetCurrentLocation = {
                    getLocation()
                }
            )

            SearchBar(onSearch = { locationName ->
                geocodeLocation(this, locationName) { location ->
                    searchedLocation.value = location
                }
            })
        }
    }

    private fun checkLocationPermission() {
        if (locationPermissionHelper.checkPermissions()) {
            getLocation()
        } else {
            locationPermissionHelper.requestPermissions(requestPermissionLauncher)
        }
    }

    private fun getLocation() {
        viewModel.getDeviceLocation(fusedLocationProviderClient)
    }
}