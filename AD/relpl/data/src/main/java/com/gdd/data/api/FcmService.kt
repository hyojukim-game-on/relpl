package com.gdd.data.api

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.fcm.CreateRegistFcmTokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmService {
    @POST("fcm/push")
    suspend fun registFcmToken(
        @Body fcmTokenRequest: CreateRegistFcmTokenRequest
    ): Result<DefaultResponse<Boolean>>
}