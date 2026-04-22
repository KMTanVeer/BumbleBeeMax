package com.bumblebeemax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage")
data class AppUsageInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val deviceLabel: String,
    val packageName: String,
    val appName: String,
    val usageDurationMs: Long,    // milliseconds
    val lastUsedTimestamp: Long,
    val date: String,             // "yyyy-MM-dd" partition key
    val timestamp: Long = System.currentTimeMillis()
)
