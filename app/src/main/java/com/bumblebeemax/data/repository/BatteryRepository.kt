package com.bumblebeemax.data.repository

import androidx.lifecycle.LiveData
import com.bumblebeemax.data.local.dao.BatteryDao
import com.bumblebeemax.data.model.BatteryInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryRepository @Inject constructor(private val dao: BatteryDao) {

    fun getLatest(deviceId: String): LiveData<BatteryInfo?> = dao.getLatest(deviceId)

    suspend fun getRecent(deviceId: String, limit: Int = 100) = dao.getRecent(deviceId, limit)

    suspend fun getSince(deviceId: String, since: Long) = dao.getSince(deviceId, since)
}
