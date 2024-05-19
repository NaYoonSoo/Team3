package com.example.moduroad.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ObstacleRetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃 설정
        .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃 설정
        .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃 설정
        .build()

    val instance: ObstacleApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ObstacleApiInterface::class.java)
    }


}


