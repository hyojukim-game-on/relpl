package com.gdd.domain.repository

import com.gdd.domain.model.point.PointRecord
import com.gdd.domain.model.user.SignUpResult
import com.gdd.domain.model.user.User
import java.io.File

interface UserRepository {
    suspend fun signIn(userUid: String, userPasswork: String): Result<User>

    suspend fun isDuplicatedPhone(phone: String): Result<Boolean>

    suspend fun isDuplicatedId(id: String): Result<Boolean>

    suspend fun isDuplicatedNickname(nickname: String): Result<Boolean>

    suspend fun signUp(phone: String, id: String, pw: String, nickname: String): Result<SignUpResult>

    suspend fun registerProfileImage(img: File, userId: Long): Result<Boolean>

    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String): Result<Boolean>

    suspend fun getCurrentPoint(userId: Long): Result<Int>

    suspend fun getPointRecord(userId: Long): Result<PointRecord>
}