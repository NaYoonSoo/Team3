package com.example.moduroad.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ObstacleApiInterface {
    @Multipart
    @POST("/detect")
    fun detectObstacle(
        @Part image: MultipartBody.Part?,
        @Part("longitude") longitude: RequestBody?,
        @Part("latitude") latitude: RequestBody?
    ): Call<ObstacleResponse>

    @POST("/register_obstacle")
    fun registerObstacle(
        @Body obstacle: ObstacleRequest
    ): Call<RegisterResponse>
}
