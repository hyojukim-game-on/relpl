package com.gdd.domain.usecase.relay.tracking

import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.repository.LocationTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRelayPathDataUseCase @Inject constructor(
    private val locationTrackingRepository: LocationTrackingRepository
) {
    suspend operator fun invoke(): List<RelayPathData> {
        return locationTrackingRepository.getAllRelayPathDataOnce()
    }
}