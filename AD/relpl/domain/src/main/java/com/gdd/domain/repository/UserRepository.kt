package com.gdd.domain.repository

import java.io.File

interface UserRepository {
    suspend fun isDuplicatedPhone(phone: String): Boolean

    suspend fun isDuplicatedId(id: String): Boolean

    suspend fun isDuplicatedNickname(nickname: String): Boolean

    suspend fun signUp(phone: String, id: String, pw: String, nickname: String)

    suspend fun registerProfileImage(img: File, userId: Long)

    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String)
}