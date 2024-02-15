package com.gdd.data.model.project

import com.gdd.data.model.PointResponse

data class DistanceProjectResponse(
    val projectId: Long,
    val projectName: String,
    val projectTotalContributer: Int,
    val projectTotalDistance: Int,
    val projectRemainingDistance: Int,
    val projectCreateDate: String,
    val projectEndDate: String,
    val projectIsPath: Boolean,
    val projectStopCoordinate: PointResponse,
    val progress: Int,
    val userMoveMemo: String?,
    val userMoveImage: String?
)
