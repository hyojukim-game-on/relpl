package com.gdd.presentation.base.location.relaying_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.gdd.domain.repository.LocationTrackingRepository
import com.gdd.presentation.R
import com.gdd.presentation.base.location.LocationProviderController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LocationTrackingService_Genseong"

@AndroidEntryPoint
abstract class LocationTrackingService : Service(), LifecycleOwner {

    @Inject
    lateinit var locationTrackingRepository: LocationTrackingRepository

    protected lateinit var notificationManager: NotificationManager
    protected lateinit var locationProviderController: LocationProviderController
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    protected abstract var locationUpdateListener: (location: Location)->Unit
    protected abstract var notiMessage: String
    protected abstract var distanceStandard: Int

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        locationProviderController = LocationProviderController(baseContext, this)
        makeNoticeChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startTracking()

        Log.d(TAG, "onStartCommand: ")
        val notificationBuilder =
            NotificationCompat
                .Builder(this, LOCATION_TRANCKING_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location)
                .setContentTitle("릴플이 당신과 함께하는 중이에요")
                .setContentText(notiMessage)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setOngoing(true)
        val noti = notificationBuilder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            startForeground(LOCATION_TRACKING_SERVICE_ID, noti, FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(LOCATION_TRACKING_SERVICE_ID, noti)
        }


        locationProviderController.startTrackingLocation(distanceStandard) { location, locationTrackingException ->
            if (location != null) {
                Log.d(TAG, "Tracking location: ${location.longitude}${location.latitude}")
                synchronized(notificationManager) {
                    notificationManager.notify(LOCATION_TRACKING_SERVICE_ID,notificationBuilder.build())
                }
                /** save real time location tracking data for RelayingFragment showing current location */
                lifecycleScope.launch {
                    val time = System.currentTimeMillis()
                    locationTrackingRepository.saveLocationTrackingData(
                        time,
                        location.latitude,
                        location.longitude
                    )
                }
                locationUpdateListener(location)
            }
        }


        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        locationProviderController.stopTracking()
        stopTracking()
    }

    private fun makeNoticeChannel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(NotificationChannel(
            LOCATION_TRANCKING_CHANNEL_ID,
            "실시간 플로깅 추적 알람",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(false)
        })
    }

    /**
     * run in onStartCommand() before location tracking
     */
    protected abstract fun startTracking()

    /**
     * run in onDestroy() after stop location tracking
     */
    protected abstract fun stopTracking()


    companion object {
        const val LOCATION_TRACKING_SERVICE_ID = 100000
        const val LOCATION_TRANCKING_CHANNEL_ID = "실시간 플로깅 위치 기록"
    }
}