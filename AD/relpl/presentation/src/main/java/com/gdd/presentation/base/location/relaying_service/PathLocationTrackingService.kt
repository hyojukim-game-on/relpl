package com.gdd.presentation.base.location.relaying_service

import android.location.Location
import com.gdd.domain.model.tracking.RelayPathData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PathLocationTrackingService : LocationTrackingService() {
    private var ioScope = CoroutineScope(Dispatchers.IO)

    override var notiMessage = "경로 기반 플로깅 중 입니다."
    override var distanceStandard = 1

    private var relayPath = listOf<RelayPathData>()

    override var locationUpdateListener = { location: Location ->
        if (relayPath.isNotEmpty()){

        }
    }

    override fun startTracking() {
        ioScope.launch {
            relayPath = locationTrackingRepository.getAllRelayPathDataOnce()
        }
    }

    override fun stopTracking() {
        ioScope.cancel()
    }
}