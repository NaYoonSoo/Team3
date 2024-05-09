package com.example.moduroad.network

import com.example.moduroad.placeAPI.NaverAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://openapi.naver.com/v1/"
    private const val BASE_URL_YOUR_API = "http://10.0.2.2:5000/"

    val instance: NaverAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NaverAPI::class.java)
    }

    val yourApiInstance: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_YOUR_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

}
