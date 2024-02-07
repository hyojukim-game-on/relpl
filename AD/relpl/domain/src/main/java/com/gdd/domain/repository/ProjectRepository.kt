package com.gdd.domain.repository

import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.IsExistDistanceRelay
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.model.relay.RelayInfoData
import com.gdd.domain.model.relay.RelayMarker

interface ProjectRepository {
    suspend fun isExistDistanceRelay(lat: Double, lng: Double): Result<IsExistDistanceRelay>

    suspend fun getAllRelayMarker(): Result<List<RelayMarker>>

    suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceRelayInfo>

    suspend fun getPathProjectInfo(projectId: Long): Result<PathRelayInfo>

    suspend fun joinRelay(projectId: Long): Result<Boolean>

    suspend fun createDistanceRelay(userId: Long, projectName: String, projectCreateDate: String, projectEndDate: String, projectTotalDistance: Int, projectStartCoordinate: Point): Result<Long>

    suspend fun recommendPath(startCoordinate: Point, endCoordinate: Point): Result<RecommendedPath>

    suspend fun createPathRelay(userId: Long,
                                projectSelectedId : String,
                                projectSelectedTotalDistance: Int,
                                projectSelectedCoordinateTotalSize: Int,
                                projectName: String,
                                projectCreateDate: String,
                                projectEndDate: String,
                                projectStartPoint: Point,
                                projectEndPoint: Point
    ): Result<Long>

    suspend fun saveProjectInfo(id: Long,
                                name: String,
                                totalContributer: Int,
                                totalDistance: Int,
                                remainDistance: Int,
                                createDate: String,
                                endDate: String,
                                isPath: Boolean,
                                endLatitude: Double,
                                endLongitude: Double)

    suspend fun getProjectInfo(): RelayInfoData
}