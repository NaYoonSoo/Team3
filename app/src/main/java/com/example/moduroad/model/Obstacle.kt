package com.example.moduroad.model

data class Obstacle(
    val type: String = "",
    val points: List<List<Double>> = emptyList()
)
