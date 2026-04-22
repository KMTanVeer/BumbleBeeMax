package com.bumblebeemax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_logs")
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val date: String,             // "yyyy-MM-dd"
    val mood: Int,                // 1-5  (1=bad, 5=great)
    val moodLabel: String,        // "Happy", "Sad", "Stressed", "Calm", "Excited"
    val habitNote: String = "",
    val screenTimeMs: Long = 0,
    val stepCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
