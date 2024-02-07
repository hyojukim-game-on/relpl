package com.gdd.presentation.model

import com.naver.maps.geometry.LatLng

data class RelayInfo(
    val id: Long,
    val name: String,
    val totalContributer: Int,
    val totalDistance: Int,
    val remainDistance: Int,
    val createDate: String,
    val endDate: String,
    val isPath: Boolean,
    val endPoint: LatLng
)
