package com.gdd.data.repository.tracking.local

import com.gdd.domain.repository.LocationTrackingRepository
import javax.inject.Inject

class LocationTrackingRepositoryImpl @Inject constructor(
    private val locationTrackingLocalDataSource: LocationTrackingLocalDataSource
): LocationTrackingRepository {
    override suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double,
        count: Int
    ) {
        locationTrackingLocalDataSource.saveLocationTrackingData(milliTime, latitude, longitude, count)
    }
}