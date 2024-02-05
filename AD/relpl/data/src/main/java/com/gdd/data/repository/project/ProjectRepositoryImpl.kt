package com.gdd.data.repository.project

import com.gdd.data.mapper.toDistanceRelayInfo
import com.gdd.data.mapper.toMarker
import com.gdd.data.mapper.toPointResponse
import com.gdd.data.repository.project.remote.ProjectRemoteDataSource
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val projectRemoteDataSource: ProjectRemoteDataSource
) : ProjectRepository{
    override suspend fun isExistDistanceRelay(lat: Double, lng: Double): Result<Boolean> {
        return projectRemoteDataSource.isExistProject(lat, lng)
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

    override suspend fun joinRelay(projectId: Long): Result<Boolean> {
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
}