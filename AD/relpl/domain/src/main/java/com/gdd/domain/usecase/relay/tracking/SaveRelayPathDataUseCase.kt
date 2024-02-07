package com.gdd.domain.usecase.relay.tracking

import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.repository.LocationTrackingRepository
import javax.inject.Inject

class SaveRelayPathDataUseCase @Inject constructor(
    private val locationTrackingRepository: LocationTrackingRepository
) {
    suspend operator fun invoke(relayPathDataList: List<RelayPathData>){
        locationTrackingRepository.insertRelayPathList(relayPathDataList)
    }
}