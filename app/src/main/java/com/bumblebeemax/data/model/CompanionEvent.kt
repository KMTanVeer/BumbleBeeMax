package com.bumblebeemax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companion_events")
data class CompanionEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val type: String,             // "REMINDER", "ALERT", "SUGGESTION", "SUMMARY"
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class DeviceProfile(
    val deviceId: String,
    val label: String,            // "My Phone" or "Wife's Phone"
    val fcmToken: String = "",
    val ownerName: String = "",
    val lastSeen: Long = 0L,
    val isOnline: Boolean = false
)
