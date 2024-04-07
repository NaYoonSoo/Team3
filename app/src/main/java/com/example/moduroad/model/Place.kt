package com.example.moduroad.model

data class Place(
    val title: String,        // 장소의 이름
    val address: String,      // 장소의 주소
    val category: String,     // 장소의 카테고리 (예: 음식점, 카페 등)
    val telephone: String?,   // 장소의 전화번호, 없을 수도 있으므로 nullable로 선언
    val link: String?,        // 장소와 관련된 웹사이트 링크, 없을 수도 있으므로 nullable로 선언
    val mapx: Double,         // 장소의 지도상 x 좌표 (경도)
    val mapy: Double,         // 장소의 지도상 y 좌표 (위도)
    val distance: Double?,    // 사용자 위치로부터의 거리 (API에서 이 정보를 제공한다면)
    val description: String?  // 장소에 대한 설명 또는 부가 정보
    // 기타 API에서 제공하는 필요한 정보들...
)
