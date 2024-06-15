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

    @GET("geocode/json")
    fun getGeocode(
        @Query("address") address: String,
        @Query("key") apiKey: String,
    ): Call<GeocodeResponse>

    @GET("geocode/json")
    fun getReverseGeocode(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String,
    ): Call<GeocodeResponse>

}