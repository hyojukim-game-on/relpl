package com.gdd.presentation.fcm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gdd.domain.usecase.fcm.RegistFcmUseCase
import com.gdd.presentation.MainActivity
import com.gdd.presentation.R
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.base.location.relaying_service.LocationTrackingService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify
import javax.inject.Inject

private const val TAG = "RelplFirebaseMessageService_ksh"

@AndroidEntryPoint
class RelplFirebaseMessageService : FirebaseMessagingService() {
    lateinit var builder: NotificationCompat.Builder

    @Inject
    lateinit var prefManager: PrefManager

    // 새로운 토큰 수신 시 서버로 전송
    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")
        super.onNewToken(token)
        prefManager.setFcmToken(token)
    }

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        makeNoticeChannel()
        remoteMessage.data.let { message ->
            message?.let {
                val messageTitle = it.get("title")
                val messageContent = it.get("body")
//                val data = remoteMessage.data
                Log.d(TAG, "data.messageTitle: ${messageTitle.toString()}")
                Log.d(TAG, "data.messageContent: ${messageContent.toString()}")
//                Log.d(TAG, "data.message: ${data.toString()}")
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val mainPendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_MUTABLE)
                builder = NotificationCompat.Builder(this, RELAY_SUCCESS__CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_path)
                    .setContentTitle(messageTitle)
                    .setContentText(messageContent)
                    .setAutoCancel(true)
                    .setContentIntent(mainPendingIntent)
                (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(
                    RELAY_SUCCESS__SERVICE_ID,builder.build())
            }
        }
    }

    private fun makeNoticeChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
            RELAY_SUCCESS__CHANNEL_ID,
            "플로깅 완료 알람",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(false)
        })
    }
    companion object {
        const val RELAY_SUCCESS__SERVICE_ID = 101
        const val RELAY_SUCCESS__CHANNEL_ID = "플로깅 완료 채널"
    }
}