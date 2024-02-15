package com.gdd.data.repository.project.remote

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.ExistBooleanData
import com.gdd.data.model.PointResponse
import com.gdd.data.model.project.DistanceProjectResponse
import com.gdd.data.model.project.IsExistDistanceResponse
import com.gdd.data.model.project.MarkerResponse
import com.gdd.data.model.project.PathProjectResponse
import com.gdd.data.model.project.RecommendPathResponse
import com.gdd.data.model.project.StopRelayingRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import java.io.File

interface ProjectRemoteDataSource {
    suspend fun isExistProject(lat: Double, lng: Double): Result<IsExistDistanceResponse>

    suspend fun getAllAvailableProject(): Result<List<MarkerResponse>>

    suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceProjectResponse>

    suspend fun getPathProjectInfo(projectId: Long): Result<PathProjectResponse>

    suspend fun joinProject(projectId: Long): Result<Long>

    suspend fun createDistanceRelay(userId: Long, projectName: String, projectCreateDate: String, projectEndDate: String, projectTotalDistance: Int, projectStartCoordinate: PointResponse): Result<Long>

    suspend fun recommendPath(startCoordinate: PointResponse, endCoordinate: PointResponse): Result<RecommendPathResponse>

    suspend fun createPathRelay(userId: Long,
                                projectSelectedId : String,
                                projectSelectedTotalDistance: Int,
                                projectSelectedCoordinateTotalSize: Int,
                                projectName: String,
                                projectCreateDate: String,
                                projectEndDate: String,
                                projectStartPoint: PointResponse,
                                projectEndPoint: PointResponse
    ): Result<Long>

    suspend fun stopProject(
        userId: Long,
        projectId: Int,
        userNickname: String, //  VARCHAR(30)
        projectName: String,// VARCHAR(30)
        moveStart: String, // VARCHAR(30) yyyy-MM-dd HH:mm
        moveEnd: String, // VARCHAR(30) yyyy-MM-dd HH:mm
        moveDistance: Int, // m단위,
        moveTime: Int, // 플로깅 실시한 시간, 분 단위
        movePath: List<PointResponse>, // 이동 경로
        moveMemo: String, // nullable, VARCHAR(30)
        projectCoordinateCurrentSize: Int  // 지나간 경로 정점의 개수, 거리 기반일 때는 무시
    ): Result<Boolean>

    suspend fun stopProjectPic(
        file: File,
        userId: Long,
        projectId: Int
    ): Result<Boolean>
}