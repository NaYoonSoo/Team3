package com.example.moduroad.network

import com.example.moduroad.model.PlacesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
interface NaverAPI {
    @GET("v1/search/local.json")
    fun searchPlaces(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int = 10,
        @Query("start") start: Int = 1
    ): Call<PlacesResponse>
}