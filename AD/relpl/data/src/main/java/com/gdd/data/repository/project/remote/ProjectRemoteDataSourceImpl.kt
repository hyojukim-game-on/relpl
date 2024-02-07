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
import com.gdd.data.toNonDefault
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

    override suspend fun joinProject(projectId: Long): Result<Boolean> {
        return projectService.joinProject(ProjectIdRequest(projectId)).toNonDefault()
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


}