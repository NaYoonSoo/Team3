// GeocodingResponse.kt
package com.example.moduroad.placeAPI

data class GeocodingResponse(
    val results: List<Result>
)

data class Result(
    val region: Region,
    val land: Land?
)

data class Region(
    val area1: Area,
    val area2: Area,
    val area3: Area,
    val area4: Area
)

data class Area(
    val name: String
)

data class Land(
    val name: String,
    val number1: String,
    val number2: String,
    val addition0: Addition,
    val addition1: Addition,
    val addition2: Addition,
    val addition3: Addition
)

data class Addition(
    val type: String,
    val value: String
)
