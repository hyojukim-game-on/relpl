package com.gdd.data.model.project

import com.gdd.data.model.PointResponse

data class RecommendPathResponse(
    val shortestId: String,
    val shortestTotalDistance : Int ,// 전체 길이
    val shortestPath: List<List<PointResponse>>
)
