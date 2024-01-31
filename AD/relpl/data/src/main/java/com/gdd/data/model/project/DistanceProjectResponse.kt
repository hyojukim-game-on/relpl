package com.gdd.data.model.project

import com.gdd.data.model.PointResponse

data class DistanceProjectResponse(
    val projectId: Long,
    val projectName: String,
    val totalContributor: Int,
    val totalDistance: Int,
    val remainDistance: Int,
    val createDate: String,
    val endDate: String,
    val isPath: Boolean,
    val stopCoordinate: PointResponse,
    val progress: Int,
    val memo: String?
)
