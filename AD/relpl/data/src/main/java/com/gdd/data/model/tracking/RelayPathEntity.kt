package com.gdd.data.model.tracking

import androidx.room.Entity

@Entity(tableName = "relay_path_table", primaryKeys = ["latitude","longitude"])
data class RelayPathEntity(
    val latitude: Double,
    val longitude: Double,
    val myVisit: Boolean,
    val beforeVisit: Boolean,
)