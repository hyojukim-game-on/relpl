package com.gdd.domain.repository

import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.IsExistDistanceRelay
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.model.relay.RelayInfoData
import com.gdd.domain.model.relay.RelayMarker
import java.io.File

interface ProjectRepository {
    suspend fun isExistDistanceRelay(lat: Double, lng: Double): Result<IsExistDistanceRelay>

    suspend fun getAllRelayMarker(): Result<List<RelayMarker>>

    suspend fun getDistanceProjectInfo(projectId: Long): Result<DistanceRelayInfo>

    suspend fun getPathProjectInfo(projectId: Long): Result<PathRelayInfo>

    suspend fun joinRelay(projectId: Long): Result<Long>

    suspend fun createDistanceRelay(userId: Long, projectName: String, projectCreateDate: String, projectEndDate: String, projectTotalDistance: Int, projectStartCoordinate: Point): Result<Long>

    suspend fun recommendPath(startCoordinate: Point, endCoordinate: Point): Result<RecommendedPath>

    suspend fun createPathRelay(userId: Long,
                                projectSelectedId : String,
                                projectSelectedTotalDistance: Int,
                                projectSelectedCoordinateTotalSize: Int,
                                projectName: String,
                                projectCreateDate: String,
                                projectEndDate: String,
                                projectStartPoint: Point,
                                projectEndPoint: Point
    ): Result<Long>

    suspend fun saveProjectInfo(id: Long,
                                name: String,
                                totalContributer: Int,
                                totalDistance: Int,
                                remainDistance: Int,
                                createDate: String,
                                endDate: String,
                                isPath: Boolean,
                                endLatitude: Double,
                                endLongitude: Double)

    suspend fun getProjectInfo(): RelayInfoData


    suspend fun stopProject(
        userId: Long,
        projectId: Int,
        userNickname: String, //  VARCHAR(30)
        projectName: String,// VARCHAR(30)
        moveStart: String, // VARCHAR(30) yyyy-MM-dd HH:mm
        moveEnd: String, // VARCHAR(30) yyyy-MM-dd HH:mm
        moveDistance: Int, // m단위,
        moveTime: Int, // 플로깅 실시한 시간, 분 단위
        movePath: List<Point>, // 이동 경로
        moveMemo: String, // nullable, VARCHAR(30)
        projectCoordinateCurrentSize: Int  // 지나간 경로 정점의 개수, 거리 기반일 때는 무시
    ): Result<Boolean>

    suspend fun stopProjectPic(
        file: File,
        userId: Long,
        projectId: Int
    ): Result<Boolean>
}