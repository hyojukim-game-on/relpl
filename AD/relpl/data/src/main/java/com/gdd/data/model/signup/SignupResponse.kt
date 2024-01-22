package com.gdd.data.model.signup

data class SignupResponse (
    val jwtRefreshToken: String,
    val userId: Long
)