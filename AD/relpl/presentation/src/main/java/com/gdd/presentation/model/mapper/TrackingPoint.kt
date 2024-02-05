package com.gdd.presentation.model.mapper

import com.naver.maps.geometry.LatLng

data class TrackingPoint(
    val timeMillis: Long,
    val latLng: LatLng
)