package com.example.moduroad.model

import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlacesViewModel : ViewModel() {
    fun searchPlaces(clientId: String, clientSecret: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val naverAPI = retrofit.create(NaverAPI::class.java)

        val call = naverAPI.searchPlaces(clientId, clientSecret, "카페", 5, 1, "random")

        call.enqueue(object : Callback<SearchResult> {
            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {
                // 성공적으로 데이터를 받아왔을 때의 처리
            }

            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                // 네트워크 요청 실패시 처리
            }
        })
    }
}
