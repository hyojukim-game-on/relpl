package com.gdd.data.repository.project

import com.gdd.data.mapper.toDistanceRelayInfo
import com.gdd.data.mapper.toIsExistDistanceRelay
import com.gdd.data.mapper.toMarker
import com.gdd.data.mapper.toPathRelayInfo
import com.gdd.data.mapper.toPointResponse
import com.gdd.data.mapper.toRecommendedPath
import com.gdd.data.repository.project.remote.ProjectRemoteDataSource
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.IsExistDistanceRelay
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectRemoteDataSource: ProjectRemoteDataSource
) : ProjectRepository{
    override suspend fun isExistDistanceRelay(lat: Double, lng: Double): Result<IsExistDistanceRelay> {
        return projectRemoteDataSource.isExistProject(lat, lng).map {
            it.toIsExistDistanceRelay()
        }
    }

    override suspend fun getAllRelayMarker(): Result<List<RelayMarker>> {
        return projectRemoteDataSource.getAllAvailableProject().map {
            it.map {marker ->
                marker.toMarker()
            }
        }
    }

    override suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceRelayInfo> {
        return projectRemoteDataSource.getDistanceProjectInfo(projectId).map {
            it.toDistanceRelayInfo()
        }
    }

    override suspend fun getPathProjectInfo(projectId: Long): Result<PathRelayInfo> {
        return projectRemoteDataSource.getPathProjectInfo(projectId).map {
            it.toPathRelayInfo()
        }
    }

    override suspend fun joinRelay(projectId: Long): Result<Long> {
        return projectRemoteDataSource.joinProject(projectId)
    }

    override suspend fun createDistanceRelay(
        userId: Long,
        projectName: String,
        projectCreateDate: String,
        projectEndDate: String,
        projectTotalDistance: Int,
        projectStartCoordinate: Point
    ): Result<Long> {
        return projectRemoteDataSource.createDistanceRelay(
            userId,
            projectName,
            projectCreateDate,
            projectEndDate,
            projectTotalDistance,
            projectStartCoordinate.toPointResponse()
        )
    }

    override suspend fun recommendPath(
        startCoordinate: Point,
        endCoordinate: Point
    ): Result<RecommendedPath> {
        return projectRemoteDataSource.recommendPath(
            startCoordinate.toPointResponse(),
            endCoordinate.toPointResponse()
        ).map {
            it.toRecommendedPath()
        }
    }

    override suspend fun createPathRelay(
        userId: Long,
        projectSelectedId: String,
        projectSelectedTotalDistance: Int,
        projectSelectedCoordinateTotalSize: Int,
        projectName: String,
        projectCreateDate: String,
        projectEndDate: String,
        projectStartPoint: Point,
        projectEndPoint: Point
    ): Result<Long> {
        return projectRemoteDataSource.createPathRelay(
            userId,
            projectSelectedId,
            projectSelectedTotalDistance,
            projectSelectedCoordinateTotalSize,
            projectName,
            projectCreateDate,
            projectEndDate,
            projectStartPoint.toPointResponse(),
            projectEndPoint.toPointResponse()
        )
    }
}