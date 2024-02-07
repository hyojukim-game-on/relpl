package com.gdd.presentation.model

import com.naver.maps.geometry.LatLng

data class RelayPath(
    val latLng: LatLng,
    val visit: Boolean
)
