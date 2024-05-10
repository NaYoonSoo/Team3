package com.example.moduroad.placeAPI

import android.content.Context
import android.widget.Toast
import com.example.moduroad.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceSearchService(private val context: Context) {
    fun searchPlaces(query: String, display: Int, start: Int, sort: String, adapter: PlacesAdapter) {
        RetrofitClient.instance.searchPlaces(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, query, display, start, sort).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    // 검색 결과가 성공적으로 반환된 경우 RecyclerView에 데이터 표시
                    val places = response.body()?.items ?: emptyList()
                    adapter.setData(places)
                } else {
                    // API 호출에 실패한 경우
                    Toast.makeText(context, "검색 결과를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                // 네트워크 요청 실패
                Toast.makeText(context, "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}