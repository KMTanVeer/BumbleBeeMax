package com.bumblebeemax.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumblebeemax.service.BatteryMonitorService
import com.bumblebeemax.service.LocationTrackingService
import com.bumblebeemax.service.UsageMonitorService
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var securePrefs: SecurePrefs

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            if (securePrefs.isSetupComplete) {
                context.startForegroundService(Intent(context, BatteryMonitorService::class.java))
                context.startForegroundService(Intent(context, LocationTrackingService::class.java))
                context.startForegroundService(Intent(context, UsageMonitorService::class.java))
            }
        }
    }
}
