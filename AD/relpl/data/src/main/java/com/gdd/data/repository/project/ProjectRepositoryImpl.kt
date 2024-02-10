package com.gdd.data.repository.project

import com.gdd.data.mapper.toDistanceRelayInfo
import com.gdd.data.mapper.toIsExistDistanceRelay
import com.gdd.data.mapper.toMarker
import com.gdd.data.mapper.toPathRelayInfo
import com.gdd.data.mapper.toPointResponse
import com.gdd.data.mapper.toRecommendedPath
import com.gdd.data.mapper.toRelayInfoData
import com.gdd.data.repository.project.local.ProjectLocalDataSource
import com.gdd.data.repository.project.remote.ProjectRemoteDataSource
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.IsExistDistanceRelay
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.model.relay.RelayInfoData
import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.repository.ProjectRepository
import java.io.File
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectRemoteDataSource: ProjectRemoteDataSource,
    private val projectLocalDataSource: ProjectLocalDataSource
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

    override suspend fun saveProjectInfo(
        id: Long,
        name: String,
        totalContributer: Int,
        totalDistance: Int,
        remainDistance: Int,
        createDate: String,
        endDate: String,
        isPath: Boolean,
        endLatitude: Double,
        endLongitude: Double
    ) {
        projectLocalDataSource.saveProjectInfo(
            id,
            name,
            totalContributer,
            totalDistance,
            remainDistance,
            createDate,
            endDate,
            isPath,
            endLatitude,
            endLongitude
        )
    }

    override suspend fun getProjectInfo(): RelayInfoData {
        return projectLocalDataSource.getProjectInfo().toRelayInfoData()
    }

    override suspend fun stopProject(
        userId: Long,
        projectId: Int,
        userNickname: String,
        projectName: String,
        moveStart: String,
        moveEnd: String,
        moveDistance: Int,
        moveTime: Int,
        movePath: List<Point>,
        moveMemo: String,
        projectCoordinateCurrentSize: Int
    ): Result<Boolean> {
        return projectRemoteDataSource.stopProject(
            userId,
            projectId,
            userNickname,
            projectName,
            moveStart,
            moveEnd,
            moveDistance,
            moveTime,
            movePath.map { it.toPointResponse() },
            moveMemo,
            projectCoordinateCurrentSize
        )
    }

    override suspend fun stopProjectPic(
        file: File,
        userId: Long,
        projectId: Int
    ): Result<Boolean> {
        return projectRemoteDataSource.stopProjectPic(file, userId, projectId)
    }
}