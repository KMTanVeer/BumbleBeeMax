package com.bumblebeemax.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bumblebeemax.data.local.dao.*
import com.bumblebeemax.data.model.*

@Database(
    entities = [
        BatteryInfo::class,
        LocationInfo::class,
        AppUsageInfo::class,
        GeofenceZone::class,
        HabitLog::class,
        CompanionEvent::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun batteryDao(): BatteryDao
    abstract fun locationDao(): LocationDao
    abstract fun appUsageDao(): AppUsageDao
    abstract fun geofenceDao(): GeofenceDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun companionEventDao(): CompanionEventDao
}
