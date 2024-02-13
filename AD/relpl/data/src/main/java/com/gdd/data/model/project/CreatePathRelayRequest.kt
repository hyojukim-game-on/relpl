package com.gdd.data.model.project

import com.gdd.data.model.PointResponse

data class CreatePathRelayRequest(
    val userId : Long, // 프로젝트를 생성한 유저
    val projectSelectedId : String, // 경로 추천시 제공한 id(이전 제공값)
    val projectSelectedTotalDistance: Int, // 프로젝트 전체 길이(이전 제공값),
    val projectSelectedCoordinateTotalSize: Int, //프로젝트 전체 위,경도 배열 크기
    val projectName: String, // VARCHAR(30)
    val projectCreateDate: String, // yyyy-MM-dd, VARCHAR(30)
    val projectEndDate : String, // yyyy-MM-dd
    val projectStartCoordinate: PointResponse, // 위, 경도
    val projectEndCoordinate: PointResponse // 위, 경도
)
