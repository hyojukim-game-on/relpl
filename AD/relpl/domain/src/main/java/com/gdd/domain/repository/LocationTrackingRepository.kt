package com.gdd.domain.repository

import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.model.tracking.TrackingData
import kotlinx.coroutines.flow.Flow

interface LocationTrackingRepository {
    suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double
    )

    fun getAllLocationTrackingData(): Flow<List<TrackingData>>

    suspend fun deleteAllLocationTrackingData()


    suspend fun insertRelayPathList(relayPathDataList: List<RelayPathData>)

    fun getAllRelayPathData(): Flow<List<RelayPathData>>

    suspend fun deleteAllRelayPathData()
}