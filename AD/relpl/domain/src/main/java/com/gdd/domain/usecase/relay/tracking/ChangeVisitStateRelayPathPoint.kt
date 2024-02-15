package com.gdd.domain.usecase.relay.tracking

import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.repository.LocationTrackingRepository
import javax.inject.Inject

class ChangeVisitStateRelayPathPoint @Inject constructor(
    private val locationTrackingRepository: LocationTrackingRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double, myVisit: Boolean, beforeVisit:Boolean){
        locationTrackingRepository.updateRelayPathPoint(RelayPathData(
            latitude, longitude, myVisit,beforeVisit
        ))
    }
}