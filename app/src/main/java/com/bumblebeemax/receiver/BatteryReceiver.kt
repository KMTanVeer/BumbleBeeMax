package com.bumblebeemax.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// Thin receiver – battery events are handled in BatteryMonitorService
// via a dynamically-registered IntentFilter. This static receiver exists
// so that ACTION_POWER_CONNECTED/DISCONNECTED still wake the monitor when
// the app is not actively running.
class BatteryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.startForegroundService(
            Intent(context, com.bumblebeemax.service.BatteryMonitorService::class.java)
        )
    }
}
