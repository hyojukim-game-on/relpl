package com.gdd.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gdd.data.dao.LocationTrackingDao
import com.gdd.data.model.tracking.LocationTrackingEntity

@Database(
    entities = [LocationTrackingEntity::class],
    version = 1
)
abstract class RelplDatabase: RoomDatabase() {
    abstract fun locationTrackingDao(): LocationTrackingDao
}