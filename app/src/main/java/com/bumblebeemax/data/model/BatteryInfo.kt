package com.bumblebeemax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_info")
data class BatteryInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val deviceLabel: String,
    val percentage: Int,
    val isCharging: Boolean,
    val chargingType: String,   // "AC", "USB", "Wireless", "None"
    val health: String,          // "Good", "Overheat", "Dead", "OverVoltage", "Unknown"
    val temperature: Float,      // Celsius
    val voltage: Int,            // mV
    val timestamp: Long = System.currentTimeMillis()
)
