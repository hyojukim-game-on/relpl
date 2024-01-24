package com.gdd.data.repository.user.remote

import com.gdd.data.api.UserService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.gdd.data.model.profile.ChangePasswordRequest
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

    override suspend fun registerProfileImage(file: File, userId: Long) {
        val map = HashMap<String, RequestBody>()
        val image = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, image)

        val id = RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString())
        map["userId"] = id

        userService.registerProfileImage(multipartBody, map)
    }

    override suspend fun changePassword(
        userId: Long,
        currentPassword: String,
        newPassword: String
    ) {
        userService.changePassword(ChangePasswordRequest(
            userId,
            currentPassword,
            newPassword
        ))
    }
}