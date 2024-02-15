package com.gdd.data.model.project

import com.gdd.data.model.PointResponse

data class CreateDistanceRelayRequest(
    val userId : Long, // 프로젝트를 생성한 유저
    val projectName: String, // VARCHAR(30)
    val projectCreateDate: String, // yyyy-MM-dd
    val projectEndDate: String, // yyyy-MM-dd
    val projectTotalDistance: Int,
    val projectStartCoordinate: PointResponse // 위, 경
)
