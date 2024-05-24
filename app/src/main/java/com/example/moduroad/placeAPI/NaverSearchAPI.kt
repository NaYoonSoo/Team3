package com.example.moduroad.placeAPI

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverSearchAPI {
    @GET("v1/search/local.json")
    fun searchPlace(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int = 1,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "random"
    ): Call<SearchResponse>
}
