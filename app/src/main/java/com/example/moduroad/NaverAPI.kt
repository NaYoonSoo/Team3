/* 네이버 검색 API를 호출하기 위한 인터페이스 정의 */

package com.example.moduroad

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverAPI {
    @GET("search/local.json")
    fun searchPlaces(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String
    ): Call<SearchResponse>
}
