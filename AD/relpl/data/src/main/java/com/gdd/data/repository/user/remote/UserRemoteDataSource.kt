package com.gdd.data.repository.user.remote

import com.gdd.data.model.signin.SignInRequest
import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.SignupRequest
import com.gdd.data.model.signup.SignupResponse
import java.io.File

interface UserRemoteDataSource {
    suspend fun signIn(signInRequest: SignInRequest): Result<SignInResponse>

    suspend fun isDuplicatedPhone(phone: String): Result<Boolean>

    suspend fun isDuplicatedId(id: String): Result<Boolean>

    suspend fun isDuplicatedNickname(nickname: String): Result<Boolean>

    suspend fun signUp(signupRequest: SignupRequest): Result<SignupResponse>

    suspend fun registerProfileImage(file: File, userId: Long): Result<Boolean>

    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String): Result<Boolean>
}