package com.example.moduroad.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ObstacleService {
    @Multipart
    @POST("detect")
    fun detectObstacle(
        @Part image: MultipartBody.Part
    ): Call<ObstacleResponse>

    @POST("registerObstacle")
    fun registerObstacle(
        @Body request: ObstacleRequest
    ): Call<RegisterResponse>
}

object ObstacleRetrofitClient {
    private const val BASE_URL = "http://192.168.236.1:5000/"
    //http://10.0.2.2:5000/"
    val instance: ObstacleService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ObstacleService::class.java)
    }
}
