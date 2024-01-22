package com.gdd.data.repository.user

import com.gdd.data.repository.user.remote.UserRemoteDataSource
import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
): UserRepository {
    override suspend fun isDuplicatedPhone(phone: String): Boolean {
        return userRemoteDataSource.isDuplicatedPhone(phone)
    }

    override suspend fun isDuplicatedId(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isDuplicatedNickname(nickname: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(phone: String, id: String, pw: String, nickname: String) {
        TODO("Not yet implemented")
    }

}