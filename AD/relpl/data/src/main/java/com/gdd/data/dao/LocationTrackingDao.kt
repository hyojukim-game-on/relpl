package com.gdd.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gdd.data.model.tracking.LocationTrackingEntity

@Dao
interface LocationTrackingDao {

    @Insert
    suspend fun insertLocationTrackingData(locationTrackingEntity: LocationTrackingEntity)

    @Query("SELECT * FROM location_tracking_table")
    suspend fun getAllLocationTrackingData(): List<LocationTrackingEntity>

    @Query("DELETE FROM location_tracking_table")
    suspend fun deleteAllLocationTrackingData()
}