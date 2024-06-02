package com.example.googlemapscapstone.api

// Data classes for parsing Directions API response
data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val steps: List<Step>,
    val duration: Duration?,
    val distance: Distance?
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


