package com.gdd.presentation.base.location.relaying_service

import android.app.Notification
import android.location.Location
import androidx.core.app.NotificationCompat
import com.gdd.presentation.R
import com.gdd.presentation.model.RelayPath
import com.gdd.presentation.model.mapper.toRelayPath
import com.gdd.presentation.model.mapper.toRelayPathData
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@AndroidEntryPoint
class PathLocationTrackingService : LocationTrackingService() {
    private var ioScope = CoroutineScope(Dispatchers.IO)

    override var notiMessage = "경로 기반 플로깅 중 입니다."
    override var distanceStandard = 1

    private var relayPath = mutableListOf<RelayPath>()
    private var beforePathPoint: RelayPath? = null

    private lateinit var leavePathNoti: NotificationCompat.Builder

    override var locationUpdateListener = { location: Location ->
        if (relayPath.isNotEmpty()) {
            // 다음 정점에 가까워 지면 방문 체크
            if (relayPath.first().latLng.distanceTo(LatLng(location)) < 10) {
                beforePathPoint = relayPath.first() //경로 이탈 확인을 위한 이전 방문 노드 저장
                ioScope.launch {
                    checkVisit(relayPath.removeFirst())
                }
            }
            // 경로 이탈 경고 로직
            if(beforePathPoint != null){
                if (calculateDistanceToLine(beforePathPoint!!.latLng,relayPath.first().latLng,LatLng(location)) > 20){
                    notificationManager.notify(PATH_LEAVE_NOTI_ID,leavePathNoti.build())
                } else {
                    notificationManager.cancel(PATH_LEAVE_NOTI_ID)
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
        initNoti()
    }

    override fun stopTracking() {
        ioScope.cancel()
    }

    private suspend fun checkVisit(relayPathPoint: RelayPath) {
        locationTrackingRepository.updateRelayPathPoint(
            relayPathPoint.copy(myVisit = true).toRelayPathData()
        )
    }

    private fun initNoti(){
        leavePathNoti = NotificationCompat
            .Builder(this, LOCATION_TRANCKING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_warning)
            .setContentTitle("경로를 벗어났어요!")
            .setContentText("경로를 벗어났습니다. 플로깅이 기록되지 않아요 원래 위치로 돌아가주세요.")
            .setAutoCancel(false)
            .setOngoing(true)
    }

    private fun calculateDistanceToLine(marker1: LatLng, marker2: LatLng, myPosition: LatLng): Double {
        val a = marker1.distanceTo(myPosition)
        val b = marker2.distanceTo(myPosition)
        val c = marker1.distanceTo(marker2)
        val s = (a+b+c) / 2.0

        return (2* sqrt(s*(s-a)*(s-b)*(s-c))) / c
    }

    companion object{
        const val PATH_LEAVE_NOTI_ID = 100001
    }
}