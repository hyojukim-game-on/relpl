package com.gdd.data.repository.user.remote

import com.gdd.data.model.signin.SignInRequest
import com.gdd.data.model.signin.SignInResponse
import java.io.File

interface UserRemoteDataSource {
    suspend fun signIn(signInRequest: SignInRequest): Result<SignInResponse>

    suspend fun isDuplicatedPhone(phone: String): Boolean

    suspend fun isDuplicatedId(id: String): Boolean

    suspend fun isDuplicatedNickname(nickname: String): Boolean

    suspend fun signUp()

    suspend fun registerProfileImage(file: File, userId: Long)

    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String)
}