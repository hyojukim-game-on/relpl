package com.gdd.data.model.token

data class ReissueRequest(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String
)
