package com.gdd.domain.usecase.user

import com.gdd.domain.model.user.User
import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class ReloadUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<User>{
        return userRepository.reloadUserInfo(userId)
    }
}