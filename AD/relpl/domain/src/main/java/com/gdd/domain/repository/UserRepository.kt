package com.gdd.domain.repository

import com.gdd.domain.model.history.History
import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.domain.model.history.HistoryInfo
import com.gdd.domain.model.point.PointRecord
import com.gdd.domain.model.user.SignUpResult
import com.gdd.domain.model.user.User
import java.io.File

interface UserRepository {
    suspend fun signIn(userUid: String, userPassword: String): Result<User>

    suspend fun isDuplicatedPhone(phone: String): Result<Boolean>

    suspend fun isDuplicatedId(id: String): Result<Boolean>

    suspend fun isDuplicatedNickname(nickname: String): Result<Boolean>

    suspend fun signUp(phone: String, id: String, pw: String, nickname: String): Result<SignUpResult>

    suspend fun registerProfileImage(img: File, userId: Long): Result<Boolean>

    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String): Result<Boolean>

    suspend fun getCurrentPoint(userId: Long): Result<Int>

    suspend fun getPointRecord(userId: Long): Result<PointRecord>

    suspend fun updateProfile(userProfilePhoto: File?, userId: Long, userNickname: String, userPhone: String): Result<Boolean>

    suspend fun getHistory(userId: Long): Result<HistoryInfo>

    suspend fun getHistoryDetail(projectId: Long): Result<HistoryDetailInfo>

    suspend fun autoLogin(userId: Long): Result<User>

    suspend fun exit(userId: Long, userPassword: String): Result<Boolean>

    suspend fun reloadUserInfo(userId: Long): Result<User>
}