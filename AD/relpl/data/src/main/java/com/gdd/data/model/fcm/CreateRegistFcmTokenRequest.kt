package com.gdd.data.model.fcm

data class CreateRegistFcmTokenRequest(
    val userId: Long,
    val fcmToken: String
)
