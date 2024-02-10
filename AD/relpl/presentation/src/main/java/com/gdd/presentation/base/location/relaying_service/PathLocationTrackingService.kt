package com.gdd.presentation.base.location.relaying_service

import android.location.Location
import com.gdd.presentation.model.RelayPath
import com.gdd.presentation.model.mapper.toRelayPath
import com.gdd.presentation.model.mapper.toRelayPathData
import com.naver.maps.geometry.LatLng
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

    private var relayPath = mutableListOf<RelayPath>()

    override var locationUpdateListener = { location: Location ->
        if (relayPath.isNotEmpty()) {
            if (relayPath.first().latLng.distanceTo(LatLng(location)) < 5) {
                ioScope.launch {
                    locationTrackingRepository.updateRelayPathPoint(
                        relayPath.removeFirst().copy(myVisit = true).toRelayPathData()
                    )
                }
            }
        }
    }

    override fun startTracking() {
        ioScope.launch {
            relayPath =
                locationTrackingRepository
                    .getAllRelayPathDataOnce()
                    .filter { !it.beforeVisit && !it.myVisit }
                    .map { it.toRelayPath() }
                    .toMutableList()
        }
    }

    override fun stopTracking() {
        ioScope.cancel()
    }
}