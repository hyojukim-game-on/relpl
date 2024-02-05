package com.gdd.domain.repository

import com.gdd.domain.model.TrackingData
import kotlinx.coroutines.flow.Flow

interface LocationTrackingRepository {
    suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double,
        count: Int
    )

    fun getAllLocationTrackingData(): Flow<List<TrackingData>>

    suspend fun deleteAllLocationTrackingData()
}