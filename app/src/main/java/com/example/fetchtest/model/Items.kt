package com.example.fetchtest.model

data class Item(
    val id: Int,
    val listId: Int,
    val name: String?
)

data class Items(
    val items: List<Item>
)

