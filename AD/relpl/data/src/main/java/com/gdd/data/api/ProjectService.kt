package com.gdd.data.api

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.ExistBooleanData
import com.gdd.data.model.ProjectIdRequest
import com.gdd.data.model.project.CreateDistanceRelayRequest
import com.gdd.data.model.project.DistanceProjectResponse
import com.gdd.data.model.project.MarkerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProjectService {

    @GET("project/exist/{lat}/{lng}")
    suspend fun isExistProject(
        @Path(value = "lat") lat: Double,
        @Path(value = "lng") lng: Double
    ): Result<DefaultResponse<ExistBooleanData>>

    @GET("project/all")
    suspend fun getAllAvailableProject(): Result<DefaultResponse<List<MarkerResponse>>>

    @GET("project//distance/{projectId}")
    suspend fun getDistanceProjectInfo(
        @Path(value = "projectId") projectId: Long
    ): Result<DefaultResponse<DistanceProjectResponse>>

    @PUT("project/join")
    suspend fun joinProject(
        @Body projectIdRequest: ProjectIdRequest
    ): Result<DefaultResponse<Boolean>>

    @POST("project/create/distance")
    suspend fun createDistanceRelay(
        @Body createDistanceRelayRequest: CreateDistanceRelayRequest
    ): Result<DefaultResponse<ProjectIdRequest>>
}