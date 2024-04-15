package com.example.moduroad

import com.example.moduroad.com.example.moduroad.network.SearchItem
import com.example.moduroad.com.example.moduroad.network.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// 네이버 검색 API와 통신하기 위한 Retrofit 인터페이스
interface NaverSearchAPI {
    @GET("v1/search/local.json") // 네이버 API의 로컬 검색 엔드포인트
    suspend fun searchPlaces(   // 'suspend' 키워드를 추가하여 코루틴과 호환되도록 함
        @Header("X-Naver-Client-Id") clientId: String,         // 네이버 API 클라이언트 ID
        @Header("X-Naver-Client-Secret") clientSecret: String, // 네이버 API 클라이언트 시크릿
        @Query("query") query: String,                         // 검색어
        @Query("display") display: Int = 10,                   // 한 페이지에 표시할 결과의 수
        @Query("start") start: Int = 1                         // 검색 시작 위치(페이지)
    ): Response<SearchResponse>  // 'Call' 대신 'Response'를 반환
}

// 네이버 검색 API 응답을 위한 데이터 클래스
data class SearchResponse(
    val items: List<SearchItem>  // 검색 결과 리스트
)

// 개별 검색 결과 아이템을 위한 데이터 클래스
data class SearchItem(
    val title: String,        // 장소의 이름
    val link: String,         // 장소의 상세 페이지 링크
    val category: String,     // 장소의 카테고리
    val description: String,  // 장소에 대한 설명
    val address: String,      // 장소의 지번 주소
    val roadAddress: String,  // 장소의 도로명 주소
    val mapx: String,         // 장소의 지도 X좌표
    val mapy: String          // 장소의 지도 Y좌표
)
