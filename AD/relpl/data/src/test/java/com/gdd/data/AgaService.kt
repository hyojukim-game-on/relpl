package com.gdd.data

import com.gdd.data.model.DefaultResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AgaService {
    @GET("users/{userId}")
    suspend fun getUserInfo(
        @Path("userId") id: String
    ): Response<DefaultResponse<User>>

    @GET("users/{userId}")
    suspend fun getUserInfoAdapter(
        @Path("userId") id: String
    ): Result<DefaultResponse<User>>
}