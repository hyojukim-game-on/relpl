package com.gdd.domain.usecase.user

import com.gdd.domain.repository.UserRepository
import java.io.File
import javax.inject.Inject

class ChangeProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(userProfilePhoto: File?, userId: Long, userNickname: String, userPhone: String): Result<Boolean>{
        return userRepository.updateProfile(userProfilePhoto, userId, userNickname, userPhone)
    }
}