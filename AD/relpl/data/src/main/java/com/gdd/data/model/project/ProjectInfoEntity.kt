package com.gdd.data.model.project

import androidx.room.Entity

@Entity(tableName = "project_info_table", primaryKeys = ["id"])
data class ProjectInfoEntity(
    val id: Long,
    val name: String,
    val totalContributer: Int,
    val totalDistance: Int,
    val remainDistance: Int,
    val createDate: String,
    val endDate: String,
    val isPath: Boolean,
    val endLatitude: Double,
    val endLongitude: Double,
)