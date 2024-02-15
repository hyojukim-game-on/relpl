package com.gdd.domain.usecase.user

import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class NicknameDuplicatedCheckUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String): Result<Boolean>{
        return userRepository.isDuplicatedNickname(nickname)
    }
}