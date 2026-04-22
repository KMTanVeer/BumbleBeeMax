package com.bumblebeemax.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bumblebeemax.data.model.GeofenceZone

@Dao
interface GeofenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(zone: GeofenceZone)

    @Update
    suspend fun update(zone: GeofenceZone)

    @Delete
    suspend fun delete(zone: GeofenceZone)

    @Query("SELECT * FROM geofence_zones ORDER BY createdAt DESC")
    fun getAll(): LiveData<List<GeofenceZone>>

    @Query("SELECT * FROM geofence_zones WHERE isActive = 1")
    suspend fun getActiveZones(): List<GeofenceZone>

    @Query("SELECT * FROM geofence_zones WHERE zoneId = :id")
    suspend fun getById(id: String): GeofenceZone?
}
