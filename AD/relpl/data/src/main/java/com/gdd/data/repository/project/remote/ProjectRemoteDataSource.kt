package com.gdd.data.repository.project.remote

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.ExistBooleanData
import com.gdd.data.model.PointResponse
import com.gdd.data.model.project.DistanceProjectResponse
import com.gdd.data.model.project.IsExistDistanceResponse
import com.gdd.data.model.project.MarkerResponse
import com.gdd.data.model.project.PathProjectResponse
import com.gdd.data.model.project.RecommendPathResponse
import retrofit2.http.GET
import retrofit2.http.Path

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
}