package com.gdd.data.model.signin

data class SignInResponse(
    val userId: Long,
    val userNickname: String,
    val userTotalCoin: Int,
    val userTotalDistance: Int,
    val userTotalReport: Int,
    val userPhone: String,
    val userImage: String?,
    val accessToken: String,
    val refreshToken: String,
)