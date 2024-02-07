package com.gdd.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gdd.data.dao.LocationTrackingDao
import com.gdd.data.dao.RelayPathDao
import com.gdd.data.model.tracking.LocationTrackingEntity
import com.gdd.data.model.tracking.RelayPathEntity

@Database(
    entities = [LocationTrackingEntity::class,RelayPathEntity::class],
    version = 1
)
abstract class RelplDataBase: RoomDatabase() {
    abstract fun locationTrackingDao(): LocationTrackingDao

    abstract fun relayPathDao(): RelayPathDao
}