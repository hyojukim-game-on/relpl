package com.gdd.domain.usecase.fcm

import com.gdd.domain.repository.FcmRepository
import javax.inject.Inject

class RegistFcmUseCase @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(
        userId: Long,
        fcmToken: String
    ): Result<Boolean> {
        return fcmRepository.registFcmToken(userId, fcmToken)
    }
}