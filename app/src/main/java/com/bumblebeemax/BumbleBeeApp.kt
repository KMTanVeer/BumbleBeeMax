package com.bumblebeemax

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BumbleBeeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)

            listOf(
                NotificationChannel(
                    CHANNEL_BATTERY, "Battery Monitoring",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Battery level and charging status alerts" },

                NotificationChannel(
                    CHANNEL_LOCATION, "Location Tracking",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "Background location tracking service" },

                NotificationChannel(
                    CHANNEL_USAGE, "App Usage",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { description = "App usage monitoring service" },

                NotificationChannel(
                    CHANNEL_GEOFENCE, "Geofence Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Enter/exit geofence zone alerts" },

                NotificationChannel(
                    CHANNEL_COMPANION, "Companion",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "Reminders, summaries and suggestions" }
            ).forEach { nm.createNotificationChannel(it) }
        }
    }

    companion object {
        const val CHANNEL_BATTERY   = "channel_battery"
        const val CHANNEL_LOCATION  = "channel_location"
        const val CHANNEL_USAGE     = "channel_usage"
        const val CHANNEL_GEOFENCE  = "channel_geofence"
        const val CHANNEL_COMPANION = "channel_companion"
    }
}
