package com.gdd.presentation.base.location.relaying_service

import android.location.Location
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DistanceLocationTrackingService : LocationTrackingService() {
    override var notiMessage = "거리 기반 플로깅 중 입니다."
    override var distanceStandard = 10

    override var locationUpdateListener = { location: Location ->

    }

    override fun startTracking() {

    }

    override fun stopTracking() {

    }
}