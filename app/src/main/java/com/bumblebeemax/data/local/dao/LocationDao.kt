package com.bumblebeemax.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bumblebeemax.data.model.LocationInfo

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: LocationInfo)

    @Query("SELECT * FROM location_info WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT 1")
    fun getLatest(deviceId: String): LiveData<LocationInfo?>

    @Query("SELECT * FROM location_info WHERE deviceId = :deviceId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(deviceId: String, limit: Int = 200): List<LocationInfo>

    @Query("SELECT * FROM location_info WHERE deviceId = :deviceId AND timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getSince(deviceId: String, since: Long): List<LocationInfo>

    @Query("DELETE FROM location_info WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}
