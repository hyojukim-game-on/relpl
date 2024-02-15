package com.gdd.data.model.project

import com.gdd.data.model.PointResponse

data class MarkerResponse(
    val projectId: Long,
    val stopCoordinate: PointResponse,
    val path: Boolean
)
