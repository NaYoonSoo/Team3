/* API로부터 받은 응답을 저장할 model class */

package com.example.moduroad.placeAPI

data class SearchResponse(
    val items: List<Place>
)

data class Place(
    val title: String,
    val address: String,
    val roadAddress: String,
    val display: Int,
    val start: Int,
    val sort: String,
    val lon: Double,
    val lat: Double
)
