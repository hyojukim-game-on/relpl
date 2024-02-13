package com.gdd.domain.repository

interface FcmRepository {
    suspend fun registFcmToken(userId: Long, fcmToken: String): Result<Boolean>
}