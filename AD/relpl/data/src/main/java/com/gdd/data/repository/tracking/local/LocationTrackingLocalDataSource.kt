package com.gdd.data.repository.tracking.local

interface LocationTrackingLocalDataSource {
    suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double,
        count: Int
    )
}