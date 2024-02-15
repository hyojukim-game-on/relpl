package com.gdd.data.api

import com.gdd.data.model.ExistBooleanData
import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.ProjectIdRequest
import com.gdd.data.model.point.TotalPointResponse
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @POST("user/login")
    suspend fun signIn(
        @Body signinRequest: SignInRequest
    ): Result<DefaultResponse<SignInResponse>>

    @POST("user/isExist/phone")
    suspend fun isDuplicatedPhone(
        @Body isDupPhoneRequest: IsDupPhoneRequest
    ): Result<DefaultResponse<ExistBooleanData>>

    @POST("user/isExist/uid")
    suspend fun isDuplicatedId(
        @Body isDupUidRequest: IsDupUidRequest
    ): Result<DefaultResponse<ExistBooleanData>>

    @GET("user/isExist/nickname/{nickname}")
    suspend fun isDuplicatedNickname(
        @Path(value = "nickname") nickname: String
    ): Result<DefaultResponse<ExistBooleanData>>

    @POST("user/signup")
    suspend fun signUp(
        @Body signupRequest: SignupRequest
    ): Result<DefaultResponse<SignupResponse>>

    @Multipart
    @POST("user/image")
    suspend fun registerProfileImage(
        @Part file: MultipartBody.Part,
        @PartMap data: HashMap<String, RequestBody>
    ): Result<DefaultResponse<Boolean>>

    @PUT("user/mypage/password")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest
    ): Result<DefaultResponse<Boolean>>

    @POST("user/mypage/coinbarcode")
    suspend fun getCurrentPointByUserId(
        @Body userIdBody: UserIdRequest
    ): Result<DefaultResponse<TotalPointResponse>>

    @POST("user/mypage/coinscore")
    suspend fun getPointRecordByUserId(
        @Body userIdBody: UserIdRequest
    ): Result<DefaultResponse<PointRecordResponse>>

    @Multipart
    @PUT("user/mypage")
    suspend fun updateProfile(
        @Part userProfilePhoto: MultipartBody.Part?,
        @PartMap data: HashMap<String, RequestBody>
    ): Result<DefaultResponse<Boolean>>

    @POST("user/history")
    suspend fun getHistory(
        @Body userIdRequest: UserIdRequest
    ): Result<DefaultResponse<HistorySummeryResponse>>

    @POST("user/history/detail")
    suspend fun getHistoryDetail(
        @Body projectIdRequest: ProjectIdRequest
    ): Result<DefaultResponse<HistoryDetailSummeryResponse>>

    @POST("user/token/reissue")
    suspend fun reissueToken(
        @Body reissueRequest: ReissueRequest
    ): Response<DefaultResponse<ReissueResponse>>

    @POST("user/autologin")
    suspend fun autoLogin(
        @Body userIdRequest: UserIdRequest
    ): Result<DefaultResponse<SignInResponse>>

    @PUT("user/mypage/exit")
    suspend fun exit(
        @Body exitRequest: ExitRequest
    ): Result<DefaultResponse<Boolean>>

    @POST("user/info")
    suspend fun loadUserInfo(
        @Body userIdRequest: UserIdRequest
    ): Result<DefaultResponse<SignInResponse>>

}