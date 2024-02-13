package com.gdd.presentation.fcm

import android.Manifest
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
import com.gdd.presentation.base.PrefManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.notification.let { message ->
            message?.let {
                val messageTitle = it.title
                val messageContent = it.body
                val data = remoteMessage.data
                Log.d(TAG, "data.messageTitle: ${messageTitle.toString()}")
                Log.d(TAG, "data.messageContent: ${messageContent.toString()}")
                Log.d(TAG, "data.message: ${data.toString()}")
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val mainPendingIntent: PendingIntent =
                    PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_MUTABLE)
                builder = NotificationCompat.Builder(this, "relpl_channel")
                    .setContentTitle(messageTitle)
                    .setContentText(messageContent)
                    .setAutoCancel(true)
                    .setContentIntent(mainPendingIntent)
                NotificationManagerCompat.from(applicationContext).apply {
                    if (ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    notify(101, builder.build())
                }
            }
        }
    }

}