package com.example.googlemapscapstone.api

import android.content.Context
import com.example.googlemapscapstone.BuildConfig
import com.example.googlemapscapstone.data.NavigationData
import com.example.googlemapscapstone.utils.showAlertDialog
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

fun drawRoute(
    context: Context,
    currentLocation: LatLng?,
    searchedLocation: LatLng?,
    markedLocation: LatLng?,
    travelMode: String,
    departureTime: String,
    onRouteDrawn: (List<LatLng>, String, NavigationData) -> Unit
) {
    if (currentLocation == null || (markedLocation == null && searchedLocation == null)) {
        showAlertDialog(
            context = context,
            title = "Empty Locations",
            message = "Current or destination location is empty. Please try again."
        )
        return
    }

    val origin = "${currentLocation.latitude},${currentLocation.longitude}"
    val destination = when {
        markedLocation != null -> "${markedLocation.latitude},${markedLocation.longitude}"
        searchedLocation != null -> "${searchedLocation.latitude},${searchedLocation.longitude}"
        else -> ""
    }
    val apiKey = BuildConfig.MAPS_API_KEY

    val service = ApiClient.getClient().create(ApiService::class.java)
    val call = service.getDirections(origin, destination, travelMode, departureTime, apiKey)

    call.enqueue(object : Callback<DirectionsResponse> {
        override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
            if (response.isSuccessful) {
                val directionsResponse = response.body()
                directionsResponse?.let { it ->
                    if (it.routes.isNotEmpty()) {
                        val polylinePoints = mutableListOf<LatLng>()
                        var routeInfo = ""
                        var navigationDetails: NavigationData? = null
                        it.routes.firstOrNull()?.legs?.forEach { leg ->
                            leg.steps.forEach { step ->
                                polylinePoints.addAll(PolyUtil.decode(step.polyline.points))
                            }
                            val duration = leg.duration?.text ?: ""
                            val distance = leg.distance?.text ?: ""
                            routeInfo = "$duration ($distance)"
                            navigationDetails = NavigationData(
                                startLocation = leg.start_address ?: "Unknown",
                                endLocation = leg.end_address ?: "Unknown",
                                travelMode = travelMode.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.ROOT
                                    ) else it.toString()
                                },
                                routeInfo = routeInfo
                            )
                        }
                        if (navigationDetails != null) {
                            onRouteDrawn(polylinePoints, routeInfo, navigationDetails!!)
                        } else {
                            showAlertDialog(
                                context = context,
                                title = "No Route Found",
                                message = "No route is available for the specified locations. Please try again with different locations."
                            )
                        }
                    } else {
                        showAlertDialog(
                            context = context,
                            title = "No Route Found",
                            message = "No route is available for the specified locations. Please try again with different locations."
                        )
                    }
                }
            }
        }

        override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
            showAlertDialog(
                context = context,
                title = "Failure",
                message = "Failure: ${t.message}"
            )
        }
    })
}
