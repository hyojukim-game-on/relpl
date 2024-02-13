package com.gdd.domain.usecase.user

import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class IdDuplicatedCheckUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String): Result<Boolean>{
        return userRepository.isDuplicatedId(id)
    }
}