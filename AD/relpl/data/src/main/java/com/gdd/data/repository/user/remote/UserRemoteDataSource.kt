package com.gdd.data.repository.user.remote

interface UserRemoteDataSource {
    suspend fun isDuplicatedPhone(phone: String): Boolean

    suspend fun isDuplicatedId(id: String): Boolean

    suspend fun isDuplicatedNickname(nickname: String): Boolean

    suspend fun signUp()
}