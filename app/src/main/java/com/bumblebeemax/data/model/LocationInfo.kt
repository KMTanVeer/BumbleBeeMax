package com.bumblebeemax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_info")
data class LocationInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val deviceLabel: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,          // metres
    val altitude: Double,
    val speed: Float,             // m/s
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
