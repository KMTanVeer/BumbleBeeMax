package com.bumblebeemax.service

import android.app.Notification
import android.app.PendingIntent
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.bumblebeemax.BumbleBeeApp
import com.bumblebeemax.MainActivity
import com.bumblebeemax.R
import com.bumblebeemax.data.local.dao.AppUsageDao
import com.bumblebeemax.data.model.AppUsageInfo
import com.bumblebeemax.data.remote.FirebaseRepository
import com.bumblebeemax.util.DateUtils
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UsageMonitorService : LifecycleService() {

    @Inject lateinit var appUsageDao: AppUsageDao
    @Inject lateinit var firebaseRepository: FirebaseRepository
    @Inject lateinit var securePrefs: SecurePrefs

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())
        startPeriodicCollection()
    }

    private fun startPeriodicCollection() {
        lifecycleScope.launch {
            while (isActive) {
                collectUsageStats()
                delay(COLLECTION_INTERVAL_MS)
            }
        }
    }

    private suspend fun collectUsageStats() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val pm = packageManager

        val cal = Calendar.getInstance()
        val endTime = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startTime = cal.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        val today = DateUtils.today()
        val deviceId    = securePrefs.deviceId
        val deviceLabel = securePrefs.deviceLabel

        val usageList = stats
            .filter { it.totalTimeInForeground > 0 }
            .map { stat ->
                val appName = runCatching {
                    pm.getApplicationLabel(
                        pm.getApplicationInfo(stat.packageName, PackageManager.GET_META_DATA)
                    ).toString()
                }.getOrDefault(stat.packageName)

                AppUsageInfo(
                    deviceId          = deviceId,
                    deviceLabel       = deviceLabel,
                    packageName       = stat.packageName,
                    appName           = appName,
                    usageDurationMs   = stat.totalTimeInForeground,
                    lastUsedTimestamp = stat.lastTimeUsed,
                    date              = today
                )
            }

        if (usageList.isNotEmpty()) {
            appUsageDao.insertAll(usageList)
            runCatching { firebaseRepository.pushUsage(deviceId, today, usageList) }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    private fun buildForegroundNotification(): Notification {
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, BumbleBeeApp.CHANNEL_USAGE)
            .setContentTitle("Usage Monitor")
            .setContentText("Tracking app usage…")
            .setSmallIcon(R.drawable.ic_usage)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent): IBinder? = super.onBind(intent)

    companion object {
        private const val NOTIFICATION_ID       = 1003
        private const val COLLECTION_INTERVAL_MS = 15 * 60 * 1000L  // 15 minutes
    }
}
