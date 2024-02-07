package com.gdd.data.repository.tracking

import android.util.Log
import com.gdd.data.mapper.toRelayPathData
import com.gdd.data.mapper.toRelayPathEntity
import com.gdd.data.mapper.toTrackData
import com.gdd.data.repository.tracking.local.LocationTrackingLocalDataSource
import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.model.tracking.TrackingData
import com.gdd.domain.repository.LocationTrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "LocationTrackingReposit_Genseong"
class LocationTrackingRepositoryImpl @Inject constructor(
    private val locationTrackingLocalDataSource: LocationTrackingLocalDataSource
): LocationTrackingRepository {
    override suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double
    ) {
        locationTrackingLocalDataSource.saveLocationTrackingData(milliTime, latitude, longitude)
    }

    override fun getAllLocationTrackingData(): Flow<List<TrackingData>> {
        return locationTrackingLocalDataSource.getAllLocationTrackingData().map { list ->
            list.map {
                Log.d(TAG, "getAllLocationTrackingData: trackingStateFlow")
                it.toTrackData()
            }
        }
    }

    override suspend fun deleteAllLocationTrackingData() {
        locationTrackingLocalDataSource.deleteAllLocationTrackingData()
    }

    override suspend fun insertRelayPathList(relayPathDataList: List<RelayPathData>) {
        locationTrackingLocalDataSource.insertRelayPathList(
            relayPathDataList.map {it.toRelayPathEntity()}
        )
    }

    override fun getAllRelayPathData(): Flow<List<RelayPathData>> {
        return locationTrackingLocalDataSource.getAllRelayPathData().map { list ->
            list.map {
                it.toRelayPathData()
            }
        }
    }

    override suspend fun deleteAllRelayPathData() {
        locationTrackingLocalDataSource.deleteAllRelayPathData()
    }

    override suspend fun updateRelayPathPoint(relayPathData: RelayPathData) {
        locationTrackingLocalDataSource.updateRelayPathPoint(
            relayPathData.toRelayPathEntity()
        )
    }

    override suspend fun getAllLocationTrackingDataOnce(): List<TrackingData> {
        return locationTrackingLocalDataSource.getAllLocationTrackingDataOnce().map {
            Log.d(TAG, "getAllLocationTrackingData: trackingStateFlow")
            it.toTrackData()
        }
    }

    override suspend fun getAllRelayPathDataOnce(): List<RelayPathData> {
        return locationTrackingLocalDataSource.getAllRelayPathDataOnce().map {
            it.toRelayPathData()
        }
    }
}