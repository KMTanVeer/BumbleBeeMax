package com.bumblebeemax.data.repository

import androidx.lifecycle.LiveData
import com.bumblebeemax.data.local.dao.AppUsageDao
import com.bumblebeemax.data.model.AppUsageInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepository @Inject constructor(private val dao: AppUsageDao) {

    fun getForDay(deviceId: String, date: String): LiveData<List<AppUsageInfo>> =
        dao.getForDay(deviceId, date)

    suspend fun getTotalScreenTimeMs(deviceId: String, date: String): Long =
        dao.getTotalScreenTimeMs(deviceId, date) ?: 0L

    suspend fun getWeeklyTotals(deviceId: String, fromDate: String) =
        dao.getWeeklyTotals(deviceId, fromDate)
}
