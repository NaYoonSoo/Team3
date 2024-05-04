package com.example.moduroad

import com.example.moduroad.network.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApiService {
    // 네이버 지역 검색 API 예시 경로
    @GET("v1/search/local.json")
    suspend fun searchPlaces(
        @Header("X-Naver-Client-Id") clientId: String,       // API 사용자 인증을 위한 클라이언트 ID
        @Header("X-Naver-Client-Secret") clientSecret: String, // API 사용자 인증을 위한 클라이언트 시크릿
        @Query("query") query: String                          // 검색어
    ): Response<SearchResponse> // 검색 결과를 담은 Response 객체를 반환
}
