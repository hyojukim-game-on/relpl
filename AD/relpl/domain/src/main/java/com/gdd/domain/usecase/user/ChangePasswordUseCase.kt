package com.gdd.domain.usecase.user

import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long, currentPassword: String, newPassword: String): Result<Boolean>{
        return userRepository.changePassword(userId, currentPassword, newPassword)
    }
}