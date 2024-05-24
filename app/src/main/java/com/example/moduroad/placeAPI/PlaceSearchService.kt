package com.example.moduroad.placeAPI

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moduroad.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceSearchService(private val context: Context, private val recyclerView: RecyclerView, private val adapter: PlacesAdapter) {
    private var isLoading = false
    private var start = 1
    private lateinit var currentQuery: String
    private var currentDisplay: Int = 10
    private lateinit var currentSort: String

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 3)) {
                    loadMore()
                }
            }
        })
    }

    fun searchPlaces(query: String, display: Int = 10, start: Int = 1, sort: String) {
        if (isLoading) return // 이미 로딩 중이라면 요청하지 않음

        this.currentQuery = query
        this.currentDisplay = display
        this.currentSort = sort
        this.start = start
        loadPlaces(true)
    }

    private fun loadMore() {
        if (isLoading) return
        start += currentDisplay
        loadPlaces(false)
    }

    private fun loadPlaces(isInitialLoad: Boolean) {
        isLoading = true
        RetrofitClient.instance.searchPlaces(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, currentQuery, currentDisplay, start, currentSort).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val newPlaces = response.body()?.items ?: emptyList()
                    if (newPlaces.isNotEmpty()) {
                        if (isInitialLoad) {
                            adapter.setData(newPlaces)
                        } else {
                            adapter.addData(newPlaces)
                        }
                    }
                } else {
                    Toast.makeText(context, "검색 결과를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
