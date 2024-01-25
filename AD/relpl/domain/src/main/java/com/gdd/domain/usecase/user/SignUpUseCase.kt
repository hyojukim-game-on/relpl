package com.gdd.domain.usecase.user

import com.gdd.domain.model.user.SignUpResult
import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        phone: String,
        id: String,
        pw: String,
        nickname: String
    ): Result<SignUpResult> {
        return userRepository.signUp(phone, id, pw, nickname)
    }
}