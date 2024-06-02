package com.example.googlemapscapstone.utils

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

// Function to geocode a location name to LatLng using Geocoder and GeocodeListener
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun geocodeLocation(context: Context, locationName: String, callback: (LatLng?) -> Unit) {
    if (locationName.isBlank()) {
        callback(null)
        return
    }

    // Make a geocoder instance and ensures that the geocoding results are localized for the device's default locale
    val geocoder = Geocoder(context, Locale.getDefault())

    // Use GeocodeListener to handle the results asynchronously
    geocoder.getFromLocationName(locationName, 1, object : Geocoder.GeocodeListener {
        override fun onGeocode(addresses: MutableList<android.location.Address>) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)
                callback(location)
            } else {
                Log.e("Geocode", "No addresses found for location: $locationName")
                callback(null)
            }
        }

        override fun onError(errorMessage: String?) {
            Log.e("Geocode", "Geocoding error: $errorMessage")
            callback(null)
        }
    })
}