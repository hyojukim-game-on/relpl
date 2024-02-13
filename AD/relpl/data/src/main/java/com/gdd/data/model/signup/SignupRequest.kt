package com.gdd.data.model.signup

data class SignupRequest(
    val userUid: String,
    val userPassword: String,
    val userNickname: String,
    val userPhone: String
)
