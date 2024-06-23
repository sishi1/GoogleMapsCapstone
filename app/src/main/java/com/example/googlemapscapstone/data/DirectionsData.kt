package com.example.googlemapscapstone.data

// Data classes for parsing Directions API response
data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val start_address: String?,
    val end_address: String?,
    val duration: Duration?,
    val distance: Distance?,
    val steps: List<Step>
)

data class Step(
    val polyline: Polyline
)

data class Polyline(
    val points: String
)

data class Duration(
    val text: String
)

data class Distance(
    val text: String
)


