package com.example.googlemapscapstone.api

import android.content.Context
import com.example.googlemapscapstone.BuildConfig
import com.example.googlemapscapstone.utils.showAlertDialog
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun fetchReverseGeocode(
    context: Context,
    latLng: LatLng,
    onResponse: (LatLng?, String?) -> Unit
) {
    val apiKey = BuildConfig.MAPS_API_KEY

    val apiClient = ApiClient.getClient().create(ApiService::class.java)
    val call = apiClient.getReverseGeocode("${latLng.latitude},${latLng.longitude}", apiKey)

    call.enqueue(object : Callback<GeocodeResponse> {
        override fun onResponse(call: Call<GeocodeResponse>, response: Response<GeocodeResponse>) {
            if (response.isSuccessful) {
                val geocodeResponse = response.body()
                if (geocodeResponse != null && geocodeResponse.results.isNotEmpty()) {
                    val address = geocodeResponse.results[0].formatted_address
                    onResponse(latLng, address)
                } else {
                    showAlertDialog(
                        context = context,
                        title = "No Results",
                        message = "No location found for the coordinates: ${latLng.latitude},${latLng.longitude}"
                    )
                    onResponse(null, null)
                }
            } else {
                showAlertDialog(
                    context = context,
                    title = "Geocode Error",
                    message = "Failed to retrieve reverse geocode: ${response.errorBody()?.string()}"
                )
                onResponse(null, null)
            }
        }

        override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
            showAlertDialog(
                context = context,
                title = "Reverse Geocode Failure",
                message = "Request failed: ${t.message}"
            )
            onResponse(null, null)
        }
    })
}




