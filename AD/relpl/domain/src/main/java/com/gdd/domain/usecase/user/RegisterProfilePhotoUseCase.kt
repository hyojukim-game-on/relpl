package com.gdd.domain.usecase.user

import com.gdd.domain.repository.UserRepository
import java.io.File
import javax.inject.Inject

class RegisterProfilePhotoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(file: File, userId: Long): Result<Boolean>{
        return userRepository.registerProfileImage(file, userId)
    }
}