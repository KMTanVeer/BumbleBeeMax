package com.bumblebeemax.data.repository

import androidx.lifecycle.LiveData
import com.bumblebeemax.data.local.dao.LocationDao
import com.bumblebeemax.data.model.LocationInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(private val dao: LocationDao) {

    fun getLatest(deviceId: String): LiveData<LocationInfo?> = dao.getLatest(deviceId)

    suspend fun getRecent(deviceId: String, limit: Int = 200) = dao.getRecent(deviceId, limit)
}
