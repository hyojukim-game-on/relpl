package com.gdd.relpl

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.gdd.presentation.fcm.RelplFirebaseMessageService
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RelplApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_MAP_CLIENT_ID)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                RelplFirebaseMessageService.RELAY_SUCCESS__CHANNEL_ID,
                "플로깅 완료 알람",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(false)
            })
    }
}