package com.gdd.data.model.project

import com.gdd.data.model.PointResponse

data class IsExistDistanceResponse(
    val exist: Boolean,
    val projectId: Long,
    val startCoordinate: PointResponse
)
