package com.gdd.domain.usecase.relay.tracking

import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.repository.LocationTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRelayPathDataUseCaseFlow @Inject constructor(
    private val locationTrackingRepository: LocationTrackingRepository
) {
    operator fun invoke(): Flow<List<RelayPathData>> {
        return locationTrackingRepository.getAllRelayPathData()
    }
}