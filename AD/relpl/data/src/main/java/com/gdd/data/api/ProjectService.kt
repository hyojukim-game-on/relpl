package com.gdd.data.api

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.ExistBooleanData
import com.gdd.data.model.ProjectIdRequest
import com.gdd.data.model.project.CreateDistanceRelayRequest
import com.gdd.data.model.project.CreatePathRelayRequest
import com.gdd.data.model.project.DistanceProjectResponse
import com.gdd.data.model.project.IsExistDistanceResponse
import com.gdd.data.model.project.MarkerResponse
import com.gdd.data.model.project.PathProjectResponse
import com.gdd.data.model.project.RecommendPathRequest
import com.gdd.data.model.project.RecommendPathResponse
import com.gdd.data.model.project.StopRelayingRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface ProjectService {

    @GET("project/exist/{lat}/{lng}")
    suspend fun isExistProject(
        @Path(value = "lat") lat: Double,
        @Path(value = "lng") lng: Double
    ): Result<DefaultResponse<IsExistDistanceResponse>>

    @GET("project/all")
    suspend fun getAllAvailableProject(): Result<DefaultResponse<List<MarkerResponse>>>

    @POST("project/check/distance")
    suspend fun getDistanceProjectInfo(
        @Body projectIdRequest: ProjectIdRequest
    ): Result<DefaultResponse<DistanceProjectResponse>>

    @POST("project/check/route")
    suspend fun getPathProjectInfo(
        @Body projectIdRequest: ProjectIdRequest
    ): Result<DefaultResponse<PathProjectResponse>>

    @POST("project/join")
    suspend fun joinProject(
        @Body projectIdRequest: ProjectIdRequest
    ): Result<DefaultResponse<ProjectIdRequest>>

    @POST("project/create/distance")
    suspend fun createDistanceRelay(
        @Body createDistanceRelayRequest: CreateDistanceRelayRequest
    ): Result<DefaultResponse<ProjectIdRequest>>

    @POST("project/recommend")
    suspend fun recommendPath(
        @Body recommendPathRequest: RecommendPathRequest
    ): Result<DefaultResponse<RecommendPathResponse>>

    @POST("project/create/route")
    suspend fun createPathRelay(
        @Body createPathRelayRequest: CreatePathRelayRequest
    ): Result<DefaultResponse<ProjectIdRequest>>

    @POST("project/stop")
    suspend fun stopProject(
        @Body stopRelayingRequest: StopRelayingRequest
    ): Result<DefaultResponse<Boolean>>

    @Multipart
    @PUT("project/stop")
    suspend fun stopProjectPic(
        @Part file: MultipartBody.Part,
        @PartMap data: HashMap<String, RequestBody>
    ): Result<DefaultResponse<Boolean>>
}