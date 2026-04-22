package com.bumblebeemax.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.bumblebeemax.data.local.dao.*
import com.bumblebeemax.data.remote.FirebaseRepository
import com.bumblebeemax.util.DateUtils
import com.bumblebeemax.util.SecurePrefs
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val batteryDao: BatteryDao,
    private val locationDao: LocationDao,
    private val appUsageDao: AppUsageDao,
    private val habitLogDao: HabitLogDao,
    private val firebaseRepository: FirebaseRepository,
    private val securePrefs: SecurePrefs
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return runCatching {
            val deviceId = securePrefs.deviceId
            val today    = DateUtils.today()

            // Push today's usage stats
            val usage = appUsageDao.getForDaySync(deviceId, today)
            if (usage.isNotEmpty()) {
                firebaseRepository.pushUsage(deviceId, today, usage)
            }

            // Push latest battery
            val battery = batteryDao.getRecent(deviceId, 1).firstOrNull()
            battery?.let { firebaseRepository.pushBattery(it) }

            // Push latest location
            val location = locationDao.getRecent(deviceId, 1).firstOrNull()
            location?.let { firebaseRepository.pushLocation(it) }

            // Push today's habit log
            val habit = habitLogDao.getForDate(deviceId, today)
            habit?.let { firebaseRepository.pushHabitLog(it) }

            // Prune local DB records older than 30 days
            val cutoff = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
            batteryDao.deleteOlderThan(cutoff)
            locationDao.deleteOlderThan(cutoff)
            appUsageDao.deleteOlderThan(cutoff)
            habitLogDao.deleteOlderThan(cutoff)

            Result.success()
        }.getOrElse { Result.retry() }
    }

    companion object {
        const val WORK_NAME = "bumblebee_sync"

        fun buildRequest(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()
    }
}
