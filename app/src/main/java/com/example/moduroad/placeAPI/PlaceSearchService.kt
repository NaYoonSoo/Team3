/* 반환된 검색 결과를 recyclerview에 표시 */

package com.example.moduroad.placeAPI

import android.content.Context
import android.widget.Toast
import com.example.moduroad.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceSearchService(private val context: Context, private val adapter: PlacesAdapter) {
    fun searchPlaces(query: String, display: Int, start: Int, sort: String, adapter: PlacesAdapter) {
        RetrofitClient.instance.searchPlaces(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, query, display, start, sort).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val places = response.body()?.items ?: emptyList()
                    adapter.setData(places)
                } else {
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