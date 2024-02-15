package com.gdd.data.repository.fcm

import com.gdd.data.repository.fcm.remote.FcmRemoteDataSource
import com.gdd.domain.repository.FcmRepository
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val fcmRemoteDataSource: FcmRemoteDataSource
) : FcmRepository{
    override suspend fun registFcmToken(userId: Long, fcmToken: String): Result<Boolean> {
        return fcmRemoteDataSource.registFcmToken(userId, fcmToken)
    }

}