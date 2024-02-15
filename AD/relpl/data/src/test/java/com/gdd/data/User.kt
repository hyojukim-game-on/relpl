package com.gdd.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "userId")
    val userId: String,
    @Json(name = "userNickname")
    val userNickname: String,
    @Json(name = "userPhone")
    val userPhone: String
)