package com.gdd.domain.usecase.relay.tracking

import com.gdd.domain.repository.LocationTrackingRepository
import javax.inject.Inject

class ClearRelayPathDataUseCase @Inject constructor(
    private val locationTrackingRepository: LocationTrackingRepository
) {
    suspend operator fun invoke(){
        locationTrackingRepository.deleteAllRelayPathData()
    }
}