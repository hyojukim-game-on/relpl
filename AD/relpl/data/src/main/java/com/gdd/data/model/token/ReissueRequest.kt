package com.gdd.data.model.token

data class ReissueRequest(
    val accessToken: String,
    val refreshToken: String
)
