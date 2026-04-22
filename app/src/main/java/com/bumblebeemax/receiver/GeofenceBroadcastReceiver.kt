package com.bumblebeemax.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.bumblebeemax.BumbleBeeApp
import com.bumblebeemax.R
import com.bumblebeemax.data.local.dao.CompanionEventDao
import com.bumblebeemax.data.model.CompanionEvent
import com.bumblebeemax.data.remote.FirebaseRepository
import com.bumblebeemax.data.model.GeofenceEvent
import com.bumblebeemax.util.SecurePrefs
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var securePrefs: SecurePrefs
    @Inject lateinit var firebaseRepository: FirebaseRepository
    @Inject lateinit var companionEventDao: CompanionEventDao

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return
        if (geofencingEvent.hasError()) return

        val transition = geofencingEvent.geofenceTransition
        val transitionLabel = when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
            Geofence.GEOFENCE_TRANSITION_EXIT  -> "EXIT"
            else -> return
        }

        geofencingEvent.triggeringGeofences?.forEach { geofence ->
            val zoneName = if (geofence.requestId.contains("zone_"))
                geofence.requestId.substringAfter("zone_").replace("_", " ")
            else
                geofence.requestId

            val title   = "Geofence: $zoneName"
            val message = "${securePrefs.deviceLabel} ${transitionLabel.lowercase()}ed zone: $zoneName"

            showGeofenceNotification(context, title, message)

            scope.launch {
                val event = GeofenceEvent(
                    zoneId      = geofence.requestId,
                    zoneName    = zoneName,
                    deviceId    = securePrefs.deviceId,
                    deviceLabel = securePrefs.deviceLabel,
                    transition  = transitionLabel
                )
                runCatching { firebaseRepository.pushGeofenceEvent(event) }

                companionEventDao.insert(
                    CompanionEvent(
                        deviceId = securePrefs.deviceId,
                        type     = "ALERT",
                        title    = title,
                        message  = message
                    )
                )
            }
        }
    }

    private fun showGeofenceNotification(context: Context, title: String, message: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, BumbleBeeApp.CHANNEL_GEOFENCE)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_geofence)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
