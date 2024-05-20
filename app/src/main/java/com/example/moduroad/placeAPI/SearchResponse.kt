/* API로부터 받은 응답을 저장할 model class */

package com.example.moduroad.placeAPI

data class SearchResponse(
    val items: List<Item>
)
data class Item(
    val title: String,
    val roadAddress: String,
    val address: String,
    val display: Int,
    val start: Int,
    val sort: String,
    val mapx: Double,
    val mapy: Double // This should match the actual response property name for latitude
       // This should match the actual response property name for longitude
)
data class Place(
    val title: String,
    val roadAddress: String,
    val address: String,
    val display: Int,
    val start: Int,
    val sort: String?,
    val lng: Double,
    val lat: Double
)
