package com.gdd.domain.usecase.point

import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentPointUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<Int>{
        return userRepository.getCurrentPoint(userId)
    }
}