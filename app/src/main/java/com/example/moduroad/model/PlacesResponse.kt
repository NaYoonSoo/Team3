package com.example.moduroad.model

data class PlacesResponse(
    val lastBuildDate: String?, // 최종 업데이트 시간
    val total: Int,             // 검색 결과의 총 개수
    val start: Int,             // 검색 결과의 시작 위치
    val display: Int,           // 검색된 검색 결과의 개수
    val items: List<Place>      // 검색 결과
)