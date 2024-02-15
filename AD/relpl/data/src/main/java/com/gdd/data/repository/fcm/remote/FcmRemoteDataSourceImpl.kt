package com.gdd.data.repository.fcm.remote

import com.gdd.data.api.FcmService
import com.gdd.data.model.fcm.CreateRegistFcmTokenRequest
import com.gdd.data.toNonDefault
import javax.inject.Inject

class FcmRemoteDataSourceImpl @Inject constructor(
    private val fcmService: FcmService
) : FcmRemoteDataSource {
    override suspend fun registFcmToken(userId: Long, fcmToken: String): Result<Boolean> {
        return fcmService.registFcmToken(
            CreateRegistFcmTokenRequest(userId, fcmToken)
        ).toNonDefault()
    }
}