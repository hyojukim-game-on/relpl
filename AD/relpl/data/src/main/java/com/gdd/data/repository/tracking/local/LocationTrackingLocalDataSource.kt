package com.gdd.data.repository.tracking.local

import com.gdd.data.model.tracking.LocationTrackingEntity
import kotlinx.coroutines.flow.Flow

interface LocationTrackingLocalDataSource {
    suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double,
        count: Int
    )

    fun getAllLocationTrackingData(): Flow<List<LocationTrackingEntity>>

    suspend fun deleteAllLocationTrackingData()
}