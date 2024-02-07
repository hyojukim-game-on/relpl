package com.gdd.data.repository.tracking.local

import com.gdd.data.dao.LocationTrackingDao
import com.gdd.data.dao.RelayPathDao
import com.gdd.data.model.tracking.LocationTrackingEntity
import com.gdd.data.model.tracking.RelayPathEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationTrackingLocalDataSourceImpl @Inject constructor(
    private val locationTrackingDao: LocationTrackingDao,
    private val relayPathDao: RelayPathDao
) : LocationTrackingLocalDataSource {
    override suspend fun saveLocationTrackingData(
        milliTime: Long,
        latitude: Double,
        longitude: Double
    ) {
        locationTrackingDao.insertLocationTrackingData(
            LocationTrackingEntity(
                milliTime, latitude, longitude
            )
        )
    }

    override fun getAllLocationTrackingData(): Flow<List<LocationTrackingEntity>> {
        return locationTrackingDao.getAllLocationTrackingData()
    }

    override suspend fun deleteAllLocationTrackingData(){
        locationTrackingDao.deleteAllLocationTrackingData()
    }

    override suspend fun insertRelayPathList(relayPathEntityList: List<RelayPathEntity>) {
        relayPathDao.insertRelayPathList(relayPathEntityList)
    }

    override fun getAllRelayPathData(): Flow<List<RelayPathEntity>> {
        return relayPathDao.getAllRelayPathData()
    }

    override suspend fun deleteAllRelayPathData() {
        relayPathDao.deleteAllRelayPathData()
    }

    override suspend fun updateRelayPathPoint(relayPathEntity: RelayPathEntity) {
        relayPathDao.updateRelayPathPoint(relayPathEntity)
    }

    override suspend fun getAllLocationTrackingDataOnce(): List<LocationTrackingEntity> {
        return locationTrackingDao.getAllLocationTrackingDataOnce()
    }

    override suspend fun getAllRelayPathDataOnce(): List<RelayPathEntity> {
        return relayPathDao.getAllRelayPathDataOnce()
    }
}