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
    private var beforePathPoint: RelayPath? = null

    override var locationUpdateListener = { location: Location ->
        if (relayPath.isNotEmpty()) {
            // 다음 정점에 가까워 지면 방문 체크
            if (relayPath.first().latLng.distanceTo(LatLng(location)) < 10) {
                ioScope.launch {
                    checkVisit(relayPath.removeFirst())
                }
            }
            // TODO 경로 이탈 경고 로직
            
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

    private suspend fun checkVisit(relayPathPoint: RelayPath) {
        locationTrackingRepository.updateRelayPathPoint(
            relayPathPoint.copy(myVisit = true).toRelayPathData()
        )
    }
}