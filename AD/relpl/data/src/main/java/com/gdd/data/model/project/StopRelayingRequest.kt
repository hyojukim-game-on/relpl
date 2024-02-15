package com.gdd.data.model.project

import com.gdd.data.model.PointResponse
import com.squareup.moshi.Json

data class StopRelayingRequest(
    val userId: Long,
    val projectId: Int,
    val userNickname: String, //  VARCHAR(30)
    val projectName: String,// VARCHAR(30)
    val moveStart: String, // VARCHAR(30) yyyy-MM-dd HH:mm
    val moveEnd: String, // VARCHAR(30) yyyy-MM-dd HH:mm
    val moveDistance: Int, // m단위,
    val moveTime: Int, // 플로깅 실시한 시간, 분 단위
    @Json(name = "userMovePath")
    val movePath: List<PointResponse>, // 이동 경로
    val moveMemo: String, // nullable, VARCHAR(30)
    val projectCoordinateCurrentSize: Int  // 지나간 경로 정점의 개수, 거리 기반일 때는 무시
)