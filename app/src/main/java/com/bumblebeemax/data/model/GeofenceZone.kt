package com.bumblebeemax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geofence_zones")
data class GeofenceZone(
    @PrimaryKey val zoneId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val alertOnEnter: Boolean = true,
    val alertOnExit: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class GeofenceEvent(
    val zoneId: String,
    val zoneName: String,
    val deviceId: String,
    val deviceLabel: String,
    val transition: String,   // "ENTER" or "EXIT"
    val timestamp: Long = System.currentTimeMillis()
)
