package com.gdd.data.api

import com.gdd.data.model.DefaultBooleanData
import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.profile.ChangePasswordRequest
import com.gdd.data.model.signup.SignupRequest
import com.gdd.data.model.signup.SignupResponse
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
    @GET("user/isExist/phone/{phone}")
    suspend fun isDuplicatedPhone(
        @Path(value = "phone") phone: String
    ): Response<DefaultResponse<DefaultBooleanData>>

    @GET("user/isExist/uid/{uid}")
    suspend fun isDuplicatedId(
        @Path(value = "uid") uid: String
    ): Response<DefaultResponse<DefaultBooleanData>>

    @GET("user/isExist/nickname/{nickname}")
    suspend fun isDuplicatedNickname(
        @Path(value = "nickname") nickname: String
    ): Response<DefaultResponse<DefaultBooleanData>>

    @POST("user/signup")
    suspend fun signUp(
        @Body signupRequest: SignupRequest
    ): Response<DefaultResponse<SignupResponse>>

    @Multipart
    @POST("user/mypage/image")
    suspend fun registerProfileImage(
        @Part file: MultipartBody.Part,
        @PartMap data: HashMap<String, RequestBody>
    )

    @PUT("user/mypage/password")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest
    )
}