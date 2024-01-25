package com.gdd.domain

data class User(
    val id: Long,
    val nickname: String,
    val totalCoin: Int,
    val totalDistance: Int,
    val totalReport: Int,
    val phone: String,
    val imageUri: String,
    val accessToken: String,
    val refreshToken: String,
)