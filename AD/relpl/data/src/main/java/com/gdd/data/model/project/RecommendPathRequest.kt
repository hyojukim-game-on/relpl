package com.gdd.data.model.project

import com.gdd.data.model.PointResponse

data class RecommendPathRequest(
    val startCoordinate: PointResponse,
    val endCoordinate: PointResponse
)
