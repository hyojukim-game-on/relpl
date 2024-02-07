package com.gdd.presentation.base.location.relaying_service

import android.location.Location
import com.gdd.domain.model.tracking.RelayPathData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class PathLocationTrackingService : LocationTrackingService() {
    private var scope = CoroutineScope(Dispatchers.IO)

    override var notiMessage = "경로 기반 플로깅 중 입니다."
    override var distanceStandard = 1

    private var relayPath = listOf<RelayPathData>()

    override var locationUpdateListener = { location: Location ->
    }

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            relayPath
        }
    }

    override fun startTracking() {

    }

    override fun stopTracking() {
        scope.cancel()
    }
}