package com.gdd.domain.repository

interface LocationTrackingRepository {
    suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double,
        count: Int
    )
}