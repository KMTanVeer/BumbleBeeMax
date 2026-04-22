package com.bumblebeemax.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bumblebeemax.data.model.HabitLog

@Dao
interface HabitLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HabitLog)

    @Update
    suspend fun update(log: HabitLog)

    @Query("SELECT * FROM habit_logs WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getAll(deviceId: String): LiveData<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE deviceId = :deviceId AND date = :date LIMIT 1")
    suspend fun getForDate(deviceId: String, date: String): HabitLog?

    @Query("SELECT * FROM habit_logs WHERE deviceId = :deviceId AND date >= :fromDate ORDER BY date ASC")
    suspend fun getSince(deviceId: String, fromDate: String): List<HabitLog>

    @Query("DELETE FROM habit_logs WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}
