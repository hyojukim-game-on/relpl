package com.gdd.domain.repository

import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.RelayMarker

interface ProjectRepository {
    suspend fun isExistDistanceRelay(lat: Double, lng: Double): Result<Boolean>

    suspend fun getAllRelayMarker(): Result<List<RelayMarker>>

    suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceRelayInfo>
}