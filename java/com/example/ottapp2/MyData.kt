package com.example.ottapp2

data class MyData(
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
)