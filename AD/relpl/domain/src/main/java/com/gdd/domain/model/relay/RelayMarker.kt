package com.gdd.domain.model.relay

import com.gdd.domain.model.Point

data class RelayMarker(
    val projectId: Long,
    val stopCoordinate: Point,
    val isPath: Boolean
)
