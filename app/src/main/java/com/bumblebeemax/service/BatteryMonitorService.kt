package com.bumblebeemax.service

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.bumblebeemax.BumbleBeeApp
import com.bumblebeemax.MainActivity
import com.bumblebeemax.R
import com.bumblebeemax.data.local.dao.BatteryDao
import com.bumblebeemax.data.model.BatteryInfo
import com.bumblebeemax.data.remote.FirebaseRepository
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BatteryMonitorService : LifecycleService() {

    @Inject lateinit var batteryDao: BatteryDao
    @Inject lateinit var firebaseRepository: FirebaseRepository
    @Inject lateinit var securePrefs: SecurePrefs

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                recordBattery(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }

    private fun recordBattery(intent: Intent) {
        val level   = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale   = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val percentage = if (scale > 0) (level * 100 / scale) else level

        val status  = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val chargingType = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC       -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB      -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "None"
        }

        val healthCode = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val health = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_GOOD         -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT     -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD         -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OverVoltage"
            BatteryManager.BATTERY_HEALTH_COLD         -> "Cold"
            else -> "Unknown"
        }

        val temp    = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

        val info = BatteryInfo(
            deviceId      = securePrefs.deviceId,
            deviceLabel   = securePrefs.deviceLabel,
            percentage    = percentage,
            isCharging    = isCharging,
            chargingType  = chargingType,
            health        = health,
            temperature   = temp,
            voltage       = voltage
        )

        lifecycleScope.launch {
            batteryDao.insert(info)
            runCatching { firebaseRepository.pushBattery(info) }
        }
    }

    private fun buildForegroundNotification(): Notification {
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, BumbleBeeApp.CHANNEL_BATTERY)
            .setContentTitle("Battery Monitor")
            .setContentText("Monitoring battery status…")
            .setSmallIcon(R.drawable.ic_battery)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent): IBinder? = super.onBind(intent)

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
