package com.gdd.domain.usecase.relay

import com.gdd.domain.model.TrackingData
import com.gdd.domain.repository.LocationTrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationTrackingDataUseCase @Inject constructor(
    private val locationTrackingRepository: LocationTrackingRepository
) {
    operator fun invoke(): Flow<List<TrackingData>> {
        return locationTrackingRepository.getAllLocationTrackingData()
    }
}