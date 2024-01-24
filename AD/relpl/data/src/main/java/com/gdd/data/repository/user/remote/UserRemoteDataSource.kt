package com.gdd.data.repository.user.remote

import java.io.File

interface UserRemoteDataSource {
    suspend fun isDuplicatedPhone(phone: String): Boolean

    suspend fun isDuplicatedId(id: String): Boolean

    suspend fun isDuplicatedNickname(nickname: String): Boolean

    suspend fun signUp()

    suspend fun registerProfileImage(file: File, userId: Long)
}