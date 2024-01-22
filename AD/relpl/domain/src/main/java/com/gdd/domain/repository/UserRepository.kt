package com.gdd.domain.repository

interface UserRepository {
    suspend fun isDuplicatedPhone(phone: String): Boolean

    suspend fun isDuplicatedId(id: String): Boolean

    suspend fun isDuplicatedNickname(nickname: String): Boolean

    suspend fun signUp(phone: String, id: String, pw: String, nickname: String)
}