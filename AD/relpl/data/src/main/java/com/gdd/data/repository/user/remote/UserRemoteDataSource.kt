package com.gdd.data.repository.user.remote

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.UserIdRequest
import com.gdd.data.model.history.HistoryDetailSummeryResponse
import com.gdd.data.model.history.HistoryResponse
import com.gdd.data.model.history.HistorySummeryResponse
import com.gdd.data.model.point.PointRecordResponse
import com.gdd.data.model.point.TotalPointResponse
import com.gdd.data.model.signin.SignInRequest
import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.SignupRequest
import com.gdd.data.model.signup.SignupResponse
import com.gdd.data.model.token.ReissueResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.File

interface UserRemoteDataSource {
    suspend fun signIn(signInRequest: SignInRequest): Result<SignInResponse>

    suspend fun isDuplicatedPhone(phone: String): Result<Boolean>

    suspend fun isDuplicatedId(id: String): Result<Boolean>

    suspend fun isDuplicatedNickname(nickname: String): Result<Boolean>

    suspend fun signUp(signupRequest: SignupRequest): Result<SignupResponse>

    suspend fun registerProfileImage(file: File, userId: Long): Result<Boolean>

    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String): Result<Boolean>

    suspend fun getCurrentPoint(userId: Long): Result<Int>

    suspend fun getPointRecord(userId: Long): Result<PointRecordResponse>

    suspend fun updateProfile(userProfilePhoto: File?, userId: Long, userNickname: String, userPhone: String): Result<Boolean>

    suspend fun getHistory(userId: Long): Result<HistorySummeryResponse>

    suspend fun getHistoryDetail(projectId: Long): Result<HistoryDetailSummeryResponse>

    suspend fun reissueToken(userId: Long, accessToken: String, refreshToken: String): Result<DefaultResponse<ReissueResponse>>

    suspend fun autoLogin(userId: Long): Result<SignInResponse>

    suspend fun exit(userId: Long, userPassword: String): Result<Boolean>

    suspend fun reloadUserInfo(userId: Long): Result<SignInResponse>
}