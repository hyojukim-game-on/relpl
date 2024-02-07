package com.gdd.data.model.tracking

import androidx.room.Entity

@Entity(tableName = "location_tracking_table", primaryKeys = ["milliTime"])
data class LocationTrackingEntity(
    val milliTime: Long,
    val latitude: Double,
    val longitude: Double
)