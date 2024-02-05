package com.gdd.data.repository.tracking.local

import com.gdd.data.dao.LocationTrackingDao
import com.gdd.data.model.tracking.LocationTrackingEntity
import javax.inject.Inject

class LocationTrackingLocalDataSourceImpl @Inject constructor(
    private val locationTrackingDao: LocationTrackingDao
) : LocationTrackingLocalDataSource {
    override suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double,
        count: Int
    ) {
        locationTrackingDao.insertLocationTrackingData(
            LocationTrackingEntity(
                milliTime, latitude, longitude, count
            )
        )
    }
}