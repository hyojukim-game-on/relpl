package com.gdd.data.repository.user.remote

import com.gdd.data.api.UserService
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val userService: UserService
): UserRemoteDataSource {
    override suspend fun isDuplicatedPhone(phone: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isDuplicatedId(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isDuplicatedNickname(nickname: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun signUp() {
        TODO("Not yet implemented")
    }
}