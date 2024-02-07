package com.gdd.data.repository.tracking.local

import androidx.room.Insert
import androidx.room.Query
import com.gdd.data.model.tracking.LocationTrackingEntity
import com.gdd.data.model.tracking.RelayPathEntity
import kotlinx.coroutines.flow.Flow

interface LocationTrackingLocalDataSource {
    suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double
    )

    fun getAllLocationTrackingData(): Flow<List<LocationTrackingEntity>>

    suspend fun getAllLocationTrackingDataOnce(): List<LocationTrackingEntity>

    suspend fun deleteAllLocationTrackingData()


    suspend fun insertRelayPathList(relayPathEntityList: List<RelayPathEntity>)

    fun getAllRelayPathData(): Flow<List<RelayPathEntity>>

    suspend fun getAllRelayPathDataOnce(): List<RelayPathEntity>

    suspend fun deleteAllRelayPathData()

    suspend fun updateRelayPathPoint(relayPathEntity: RelayPathEntity)
}