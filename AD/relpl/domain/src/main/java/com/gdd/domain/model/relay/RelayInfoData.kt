package com.gdd.domain.model.relay

import com.gdd.domain.model.Point

data class RelayInfoData(
    val id: Long,
    val name: String,
    val totalContributer: Int,
    val totalDistance: Int,
    val remainDistance: Int,
    val createDate: String,
    val endDate: String,
    val isPath: Boolean,
    val endPoint: Point
)
