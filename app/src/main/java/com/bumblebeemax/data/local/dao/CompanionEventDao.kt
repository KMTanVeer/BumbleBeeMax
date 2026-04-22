package com.bumblebeemax.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bumblebeemax.data.model.CompanionEvent

@Dao
interface CompanionEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CompanionEvent)

    @Update
    suspend fun update(event: CompanionEvent)

    @Query("SELECT * FROM companion_events WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getAll(deviceId: String): LiveData<List<CompanionEvent>>

    @Query("SELECT * FROM companion_events WHERE deviceId = :deviceId AND isRead = 0 ORDER BY timestamp DESC")
    fun getUnread(deviceId: String): LiveData<List<CompanionEvent>>

    @Query("UPDATE companion_events SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("UPDATE companion_events SET isRead = 1 WHERE deviceId = :deviceId")
    suspend fun markAllRead(deviceId: String)

    @Query("DELETE FROM companion_events WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}
