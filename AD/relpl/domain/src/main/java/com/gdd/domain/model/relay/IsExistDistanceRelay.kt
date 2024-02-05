package com.gdd.domain.model.relay

import com.gdd.domain.model.Point

data class IsExistDistanceRelay (
    val isExit: Boolean,
    val projectId: Long,
    val projectCoordinate: Point
)