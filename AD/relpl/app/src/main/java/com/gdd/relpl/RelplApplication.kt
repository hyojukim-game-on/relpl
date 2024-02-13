package com.gdd.relpl

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.gdd.presentation.base.location.LocationTrackingService
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RelplApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_MAP_CLIENT_ID)
    }
}