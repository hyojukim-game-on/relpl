package com.gdd.domain.usecase.user

import com.gdd.domain.model.user.User
import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(userUid: String, userPassword: String): Result<User>{
        return userRepository.signIn(userUid,userPassword)
    }
}