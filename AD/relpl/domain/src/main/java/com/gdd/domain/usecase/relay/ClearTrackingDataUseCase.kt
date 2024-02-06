package com.gdd.domain.usecase.relay

import com.gdd.domain.repository.LocationTrackingRepository
import javax.inject.Inject

class ClearTrackingDataUseCase @Inject constructor(
    private val trackingRepository: LocationTrackingRepository
) {
    suspend operator fun invoke(){
        trackingRepository.deleteAllLocationTrackingData()
    }
}