package com.gdd.data.repository.project.remote

import com.gdd.data.api.ProjectService
import com.gdd.data.dao.ProjectInfoDao
import com.gdd.data.model.PointResponse
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
import com.gdd.data.toNonDefault
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ProjectRemoteDataSourceImpl @Inject constructor(
    private val projectService: ProjectService,
): ProjectRemoteDataSource {
    override suspend fun isExistProject(lat: Double, lng: Double): Result<IsExistDistanceResponse> {
        return projectService.isExistProject(lng, lat)
            .toNonDefault()

    }

    override suspend fun getAllAvailableProject(): Result<List<MarkerResponse>> {
        return projectService.getAllAvailableProject().toNonDefault()
    }

    override suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceProjectResponse> {
        return projectService.getDistanceProjectInfo(ProjectIdRequest(projectId)).toNonDefault()
    }

    override suspend fun getPathProjectInfo(projectId: Long): Result<PathProjectResponse> {
        return projectService.getPathProjectInfo(ProjectIdRequest(projectId)).toNonDefault()
    }

    override suspend fun joinProject(projectId: Long): Result<Long> {
        return projectService.joinProject(ProjectIdRequest(projectId)).toNonDefault().map {
            it.projectId
        }
    }

    override suspend fun createDistanceRelay(
        userId: Long,
        projectName: String,
        projectCreateDate: String,
        projectEndDate: String,
        projectTotalDistance: Int,
        projectStartCoordinate: PointResponse
    ): Result<Long> {
        return projectService.createDistanceRelay(
            CreateDistanceRelayRequest(
                userId,
                projectName,
                projectCreateDate,
                projectEndDate,
                projectTotalDistance,
                projectStartCoordinate
            )
        ).toNonDefault().map {
            it.projectId
        }
    }

    override suspend fun recommendPath(
        startCoordinate: PointResponse,
        endCoordinate: PointResponse
    ): Result<RecommendPathResponse> {
        return projectService.recommendPath(RecommendPathRequest(startCoordinate, endCoordinate)).toNonDefault()
    }

    override suspend fun createPathRelay(
        userId: Long,
        projectSelectedId: String,
        projectSelectedTotalDistance: Int,
        projectSelectedCoordinateTotalSize: Int,
        projectName: String,
        projectCreateDate: String,
        projectEndDate: String,
        projectStartPoint: PointResponse,
        projectEndPoint: PointResponse
    ): Result<Long> {
        return projectService.createPathRelay(
            CreatePathRelayRequest(
                userId,
                projectSelectedId,
                projectSelectedTotalDistance,
                projectSelectedCoordinateTotalSize,
                projectName,
                projectCreateDate,
                projectEndDate,
                projectStartPoint,
                projectEndPoint
            )
        ).toNonDefault()
            .map {
                it.projectId
            }
    }

    override suspend fun stopProject(
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
        projectCoordinateCurrentSize: Int // 지나간 경로 정점의 개수
    ): Result<Boolean> {
        return projectService.stopProject(
            StopRelayingRequest(
                userId,
                projectId,
                userNickname,
                projectName,
                moveStart,
                moveEnd,
                moveDistance,
                moveTime,
                movePath,
                moveMemo,
                projectCoordinateCurrentSize
            )
        ).toNonDefault()
    }

    override suspend fun stopProjectPic(
        file: File,
        userId: Long,
        projectId: Int
    ): Result<Boolean> {
        val map = HashMap<String, RequestBody>()
        val image = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("moveImage", file.name, image)
        val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val projectIdBody = projectId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        map["userId"] = userIdBody
        map["projectId"] = projectIdBody
        return projectService.stopProjectPic(multipartBody, map).toNonDefault()
    }

}