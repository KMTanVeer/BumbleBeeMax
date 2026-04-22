package com.bumblebeemax.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.bumblebeemax.BumbleBeeApp
import com.bumblebeemax.R
import com.bumblebeemax.data.local.dao.CompanionEventDao
import com.bumblebeemax.data.model.CompanionEvent
import com.bumblebeemax.util.SecurePrefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject lateinit var companionEventDao: CompanionEventDao
    @Inject lateinit var securePrefs: SecurePrefs

    private val serviceJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        securePrefs.fcmToken = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data  = message.data
        val title = message.notification?.title ?: data["title"] ?: "BumbleBeeMax"
        val body  = message.notification?.body  ?: data["body"]  ?: ""
        val type  = data["type"] ?: "ALERT"

        showNotification(title, body)

        val event = CompanionEvent(
            deviceId  = securePrefs.deviceId,
            type      = type,
            title     = title,
            message   = body
        )
        scope.launch { companionEventDao.insert(event) }
    }

    private fun showNotification(title: String, body: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, BumbleBeeApp.CHANNEL_COMPANION)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_companion)
            .setAutoCancel(true)
            .build()
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
