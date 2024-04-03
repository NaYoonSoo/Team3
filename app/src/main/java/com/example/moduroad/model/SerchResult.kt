package com.example.moduroad.model

data class SearchResult(
    val items: List<Item>
) {
    data class Item(
        val title: String,
        val link: String,
        val category: String,
        val description: String,
        val telephone: String,
        val address: String
    )
}
