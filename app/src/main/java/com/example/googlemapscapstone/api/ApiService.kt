package com.example.googlemapscapstone.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("departure_time") departureTime: String,
        @Query("key") apiKey: String,
    ): Call<DirectionsResponse>
}