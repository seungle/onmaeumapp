package com.example.onmaeumapp.data.model

data class Meditation(
    val id: Long = 0,
    val title: String,
    val description: String,
    val duration: Int,
    val category: String,
    val difficulty: String,
    val audioUrl: String,
    val imageUrl: String
) 