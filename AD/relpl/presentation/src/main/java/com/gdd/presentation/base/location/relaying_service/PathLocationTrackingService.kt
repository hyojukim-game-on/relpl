package com.gdd.presentation.base.location.relaying_service

import android.location.Location
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PathLocationTrackingService : LocationTrackingService() {
    override var notiMessage = "경로 기반 플로깅 중 입니다."
    override var distanceStandard = 1
    override var locationUpdateListener = { location: Location ->

    }

    override fun startTracking() {

    }

    override fun stopTracking() {

    }
}