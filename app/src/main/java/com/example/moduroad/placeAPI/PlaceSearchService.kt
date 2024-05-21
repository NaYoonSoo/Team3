package com.example.moduroad.placeAPI

import android.content.Context
import android.location.Location
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.example.moduroad.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceSearchService(private val context: Context, private val adapter: PlacesAdapter) {

    var isLoading = false
    private var currentLocation: Location? = null

    fun searchPlaces(query: String, display: Int, start: Int, sort: String, latitude: Double, longitude: Double) {
        if (isLoading) return
        isLoading = true

        // 현재 위치 업데이트
        currentLocation = Location("").apply {
            setLatitude(latitude)
            setLongitude(longitude)
        }

        // Retrofit 호출
        RetrofitClient.instance.searchPlacesByLocation(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, query, display, start, sort).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val places = response.body()?.items?.map {
                        Place(
                            title = Html.fromHtml(it.title).toString(),  // HTML 태그 제거
                            roadAddress = it.roadAddress,
                            address = it.address,
                            display = it.display,
                            sort = it.sort,
                            start = it.start,
                            lat = it.mapy / 1e6,  // 실제 속성 이름으로 대체
                            lng = it.mapx / 1e6   // 실제 속성 이름으로 대체
                        )
                    } ?: emptyList()

                    // 현재 위치가 null이 아닌 경우 거리순으로 정렬
                    val sortedPlaces = currentLocation?.let { location ->
                        places.sortedBy { place ->
                            val targetLocation = Location("").apply {
                                setLatitude(place.lat)
                                setLongitude(place.lng)
                            }
                            location.distanceTo(targetLocation)
                        }
                    } ?: places

                    if (start == 1) {
                        adapter.setData(sortedPlaces)
                    } else {
                        adapter.addData(sortedPlaces)
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

// Extension 함수: 주어진 위도와 경도 사이의 거리를 계산
fun Location.distanceTo(latitude: Double, longitude: Double): Float {
    // 타겟 위치 객체 생성
    val targetLocation = Location("").apply {
        this.latitude = latitude
        this.longitude = longitude
    }

    // 내장된 distanceTo 메서드를 사용하여 거리 계산
    return this.distanceTo(targetLocation)
}
