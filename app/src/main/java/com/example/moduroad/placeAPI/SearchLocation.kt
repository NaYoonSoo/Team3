// 출발목적지 장소api 넣다가 실패한거 일단 적어놓음ㅎ

package com.example.moduroad.placeAPI

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moduroad.databinding.ActivityLocationInputBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.moduroad.BuildConfig

class SearchLocation : AppCompatActivity() {

    private lateinit var placeAdapter: PlacesAdapter
    private lateinit var binding: ActivityLocationInputBinding
    private lateinit var placeSearchService: NaverAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        placeSearchService = RetrofitClient.instance

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchLocation(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 키보드의 엔터 버튼을 눌렀을 때 검색 실행
        binding.searchInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchLocation(v.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerView() {
        placeAdapter = PlacesAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = placeAdapter
    }

    private fun searchLocation(query: String) {
        placeSearchService.searchPlaces(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, query, 10, 1, "random").enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val places = response.body()?.items ?: emptyList()
                    placeAdapter.setData(places)
                } else {
                    showError("검색 결과를 불러오는 데 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showError("네트워크 오류: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {

    }
}
