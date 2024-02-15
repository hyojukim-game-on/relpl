package com.gdd.domain.usecase.relay.tracking

import com.gdd.domain.model.tracking.TrackingData
import com.gdd.domain.repository.LocationTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationTrackingDataUseCaseFlow @Inject constructor(
    private val locationTrackingRepository: LocationTrackingRepository
) {
    operator fun invoke(): Flow<List<TrackingData>> {
        return locationTrackingRepository.getAllLocationTrackingData()
    }
}