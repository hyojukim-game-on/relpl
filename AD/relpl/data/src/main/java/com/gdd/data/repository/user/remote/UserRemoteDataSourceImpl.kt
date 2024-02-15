package com.gdd.data.repository.user.remote

import android.util.Log
import com.gdd.data.api.UserService
import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.ProjectIdRequest
import com.gdd.data.model.UserIdRequest
import com.gdd.data.model.exit.ExitRequest
import com.gdd.data.model.history.HistoryDetailSummeryResponse
import com.gdd.data.model.history.HistoryResponse
import com.gdd.data.model.history.HistorySummeryResponse
import com.gdd.data.model.point.PointRecordResponse
import com.gdd.data.model.profile.ChangePasswordRequest
import com.gdd.data.model.signin.SignInRequest
import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.IsDupPhoneRequest
import com.gdd.data.model.signup.IsDupUidRequest
import com.gdd.data.model.signup.SignupRequest
import com.gdd.data.model.signup.SignupResponse
import com.gdd.data.model.token.ReissueRequest
import com.gdd.data.model.token.ReissueResponse
import com.gdd.data.toNonDefault
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

private const val TAG = "UserRemoteDataSourceImp_Genseong"
class UserRemoteDataSourceImpl @Inject constructor(
    private val userService: UserService
) : UserRemoteDataSource {
    override suspend fun signIn(signInRequest: SignInRequest): Result<SignInResponse> {
        return userService.signIn(signInRequest).toNonDefault()
    }

    override suspend fun isDuplicatedPhone(phone: String): Result<Boolean> {
        return userService.isDuplicatedPhone(IsDupPhoneRequest(phone))
            .toNonDefault()
            .map {
                it.isExist
            }
    }

    override suspend fun isDuplicatedId(id: String): Result<Boolean> {
        return userService.isDuplicatedId(IsDupUidRequest(id))
            .toNonDefault()
            .map {
                it.isExist
            }
    }

    override suspend fun isDuplicatedNickname(nickname: String): Result<Boolean> {
        return userService.isDuplicatedNickname(nickname)
            .toNonDefault()
            .map {
                it.isExist
            }
    }

    override suspend fun signUp(signupRequest: SignupRequest): Result<SignupResponse> {
        return userService.signUp(signupRequest).toNonDefault()
    }

    override suspend fun registerProfileImage(file: File, userId: Long): Result<Boolean> {
        val map = HashMap<String, RequestBody>()
        val image = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, image)

        val id = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())
        map["userId"] = id

        return userService.registerProfileImage(multipartBody, map).toNonDefault()
    }

    override suspend fun changePassword(
        userId: Long,
        currentPassword: String,
        newPassword: String
    ): Result<Boolean> {
        return userService.changePassword(
            ChangePasswordRequest(
                userId,
                currentPassword,
                newPassword
            )
        ).toNonDefault()
    }

    override suspend fun getCurrentPoint(userId: Long): Result<Int> {
        return userService.getCurrentPointByUserId(
            UserIdRequest(userId)
        ).toNonDefault().map {
            it.userTotalCoin
        }
    }

    override suspend fun getPointRecord(userId: Long): Result<PointRecordResponse> {
        return userService.getPointRecordByUserId(
            UserIdRequest(userId)
        ).toNonDefault()
    }

    override suspend fun updateProfile(
        userProfilePhoto: File?,
        userId: Long,
        userNickname: String,
        userPhone: String
    ): Result<Boolean> {
        val map = HashMap<String, RequestBody>()
        val id = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())
        val nickname = RequestBody.create("text/plain".toMediaTypeOrNull(), userNickname)
        val phone = RequestBody.create("text/plain".toMediaTypeOrNull(), userPhone)

        map["userId"] = id
        map["userNickname"] = nickname
        map["userPhone"] = phone

        return if (userProfilePhoto != null) {
            Log.d(TAG, "updateProfile: 1")
            val image = userProfilePhoto.asRequestBody("image/jpg".toMediaTypeOrNull())
            val multipartBody =
                MultipartBody.Part.createFormData("userProfilePhoto", userProfilePhoto.name, image)

            Log.d(TAG, "updateProfile: ${multipartBody.body.toString()}")
            userService.updateProfile(multipartBody, map).toNonDefault()
        }else{
            Log.d(TAG, "updateProfile: 2")
            userService.updateProfile(null, map).toNonDefault()
        }
    }

    override suspend fun getHistory(userId: Long): Result<HistorySummeryResponse> {
        return userService.getHistory(
            UserIdRequest(userId)
        ).toNonDefault()
    }

    override suspend fun getHistoryDetail(projectId: Long): Result<HistoryDetailSummeryResponse> {
        return userService.getHistoryDetail(
            ProjectIdRequest(projectId)
        ).toNonDefault()
    }

    override suspend fun reissueToken(
        userId: Long,
        accessToken: String,
        refreshToken: String
    ): Result<DefaultResponse<ReissueResponse>> {
        return userService.reissueToken(ReissueRequest(
            userId,
            accessToken,
            refreshToken
        )).let { response ->
            if (response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        }
    }

    override suspend fun autoLogin(userId: Long): Result<SignInResponse> {
        return userService.autoLogin(UserIdRequest(userId)).toNonDefault()
    }

    override suspend fun exit(userId: Long, userPassword: String): Result<Boolean> {
        return userService.exit(ExitRequest(userId, userPassword)).toNonDefault()
            .map { it }
    }

    override suspend fun reloadUserInfo(userId: Long): Result<SignInResponse> {
        return userService.loadUserInfo(UserIdRequest(userId)).toNonDefault()
    }
}