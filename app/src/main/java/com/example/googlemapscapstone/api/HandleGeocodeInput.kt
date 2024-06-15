package com.example.googlemapscapstone.api

import android.content.Context
import com.example.googlemapscapstone.utils.showAlertDialog
import com.google.android.gms.maps.model.LatLng

fun handleGeocodeInput(
    context: Context,
    input: String,
    onResponse: (LatLng?, String?) -> Unit
) {
    // Regular expression to match latitude and longitude
    val latLngPattern = Regex("^[-+]?\\d{1,2}\\.\\d+\\s*,\\s*[-+]?\\d{1,3}\\.\\d+$")

    if (latLngPattern.matches(input.trim())) {
        val latLng = input.split(",")
        val lat = latLng[0].trim().toDoubleOrNull()
        val lng = latLng[1].trim().toDoubleOrNull()
        if (lat != null && lng != null) {
            fetchReverseGeocode(context, LatLng(lat, lng), onResponse)
        } else {
            showAlertDialog(
                context = context,
                title = "Invalid Coordinates",
                message = "The coordinates provided are invalid. Please try again."
            )
            onResponse(null, null)
        }
    } else {
        fetchGeocode(context, input, onResponse)
    }
}
