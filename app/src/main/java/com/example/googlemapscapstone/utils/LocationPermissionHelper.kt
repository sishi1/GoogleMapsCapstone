package com.example.googlemapscapstone.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.googlemapscapstone.viewmodel.GoogleMapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient

object LocationPermissionHelper {

    fun checkPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowRequestPermissionRationale(context: Context): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun requestPermissions(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun getLocation(viewModel: GoogleMapsViewModel, fusedLocationProviderClient: FusedLocationProviderClient) {
        viewModel.getDeviceLocation(fusedLocationProviderClient)
    }
}