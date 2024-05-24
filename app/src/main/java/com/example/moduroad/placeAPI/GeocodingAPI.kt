package com.example.moduroad.placeAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GeocodingAPI {
    @GET("map-geocode/v2/geocode")
    fun getGeocodingData(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
        @Query("coords") coords: String,
        @Query("output") output: String = "json"
    ): Call<GeocodingResponse>
}
