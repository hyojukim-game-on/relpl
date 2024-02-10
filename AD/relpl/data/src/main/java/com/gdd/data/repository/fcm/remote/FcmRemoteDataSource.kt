package com.gdd.data.repository.fcm.remote

interface FcmRemoteDataSource {
    suspend fun registFcmToken(userId: Long, fcmToken: String): Result<Boolean>
}