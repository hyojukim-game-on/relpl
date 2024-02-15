package com.gdd.domain.model.relay

import com.gdd.domain.model.Point

data class RecommendedPath(
    val shortestId: String,
    val shortestTotalDistance : Int,
    val shortestPath: List<Point>,
    val recommendId: String,
    val recommendTotalDistance: Int,
    val recommendPath: List<Point>
)
