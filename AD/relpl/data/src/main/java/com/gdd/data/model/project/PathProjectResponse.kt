package com.gdd.data.model.project

import com.gdd.data.model.PointResponse
import com.squareup.moshi.Json

data class PathProjectResponse(
    val projectId: Long, // 프로젝트 pk
    val projectName: String, // 프로젝트 이름
    val projectTotalContributer: Int, // 프로젝트 참여 유저 수
    val projectTotalDistance: Int, // 프로젝트 총 거리
    val projectRemainingDistance: Int, // 남아 있는 프로젝트 거리
    val projectCreateDate: String, // 프로젝트 처음 생성한 날짜
    val projectEndDate: String, // 프로젝트 종료한 날짜
    val projectIsPath: Boolean, // 경로-true , 거리 -false
    val projectStopCoordinate: PointResponse, // 중단점 좌표
    @Json(name = "progress")
    val projectProgress: Int, //진행률 서버 계산 필요
    val userMoveMemo: String?,
    val userMoveImage: String?,
    val projectRoute: List<PointResponse>
)