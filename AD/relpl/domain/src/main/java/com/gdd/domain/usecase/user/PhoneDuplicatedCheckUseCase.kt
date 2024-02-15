package com.gdd.domain.usecase.user

import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class PhoneDuplicatedCheckUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(phone: String): Result<Boolean>{
        return userRepository.isDuplicatedPhone(phone)
    }
}