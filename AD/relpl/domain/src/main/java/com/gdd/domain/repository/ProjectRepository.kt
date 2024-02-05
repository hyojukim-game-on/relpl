package com.gdd.domain.repository

import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.model.relay.RelayMarker

interface ProjectRepository {
    suspend fun isExistDistanceRelay(lat: Double, lng: Double): Result<Boolean>

    suspend fun getAllRelayMarker(): Result<List<RelayMarker>>

    suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceRelayInfo>

    suspend fun joinRelay(projectId: Long): Result<Boolean>

    suspend fun createDistanceRelay(userId: Long, projectName: String, projectCreateDate: String, projectEndDate: String, projectTotalDistance: Int, projectStartCoordinate: Point): Result<Long>

    suspend fun recommendPath(startCoordinate: Point, endCoordinate: Point): Result<RecommendedPath>
}