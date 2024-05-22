package com.example.moduroad.placeAPI

import com.example.moduroad.network.ApiInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://openapi.naver.com/v1/"
    //private const val BASE_URL_YOUR_API = "http://10.0.2.2:5000/"
    private const val BASE_URL_YOUR_API = "http://192.168.236.1:5000/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(40, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build()

    val instance: NaverAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NaverAPI::class.java)
    }

    val yourApiInstance: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_YOUR_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}