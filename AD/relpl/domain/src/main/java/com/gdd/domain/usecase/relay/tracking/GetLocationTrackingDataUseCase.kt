package com.gdd.domain.usecase.relay.tracking

import com.gdd.domain.model.tracking.TrackingData
import com.gdd.domain.repository.LocationTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationTrackingDataUseCase @Inject constructor(
    private val locationTrackingRepository: LocationTrackingRepository
) {
    suspend operator fun invoke(): List<TrackingData> {
        return locationTrackingRepository.getAllLocationTrackingDataOnce()
    }
}