package com.bumblebeemax.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bumblebeemax.data.model.AppUsageInfo

@Dao
interface AppUsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AppUsageInfo>)

    @Query("SELECT * FROM app_usage WHERE deviceId = :deviceId AND date = :date ORDER BY usageDurationMs DESC")
    fun getForDay(deviceId: String, date: String): LiveData<List<AppUsageInfo>>

    @Query("SELECT * FROM app_usage WHERE deviceId = :deviceId AND date = :date ORDER BY usageDurationMs DESC")
    suspend fun getForDaySync(deviceId: String, date: String): List<AppUsageInfo>

    @Query("SELECT SUM(usageDurationMs) FROM app_usage WHERE deviceId = :deviceId AND date = :date")
    suspend fun getTotalScreenTimeMs(deviceId: String, date: String): Long?

    @Query("SELECT date, SUM(usageDurationMs) as usageDurationMs FROM app_usage WHERE deviceId = :deviceId AND date >= :fromDate GROUP BY date ORDER BY date ASC")
    suspend fun getWeeklyTotals(deviceId: String, fromDate: String): List<DailyTotal>

    @Query("DELETE FROM app_usage WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    data class DailyTotal(val date: String, val usageDurationMs: Long)
}
