package com.gdd.domain.usecase.user

import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class ExitUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long, userPassword: String): Result<Boolean>{
        return userRepository.exit(userId, userPassword)
    }
}