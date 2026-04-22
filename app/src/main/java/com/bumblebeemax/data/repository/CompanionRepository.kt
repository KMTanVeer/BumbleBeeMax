package com.bumblebeemax.data.repository

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import com.bumblebeemax.BumbleBeeApp
import com.bumblebeemax.R
import com.bumblebeemax.data.local.dao.CompanionEventDao
import com.bumblebeemax.data.local.dao.HabitLogDao
import com.bumblebeemax.data.model.CompanionEvent
import com.bumblebeemax.data.model.HabitLog
import com.bumblebeemax.data.remote.FirebaseRepository
import com.bumblebeemax.util.DateUtils
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanionRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventDao: CompanionEventDao,
    private val habitLogDao: HabitLogDao,
    private val firebaseRepository: FirebaseRepository,
    private val securePrefs: SecurePrefs
) {

    // ── Companion Events ──────────────────────────────────────────────

    fun getAll(deviceId: String): LiveData<List<CompanionEvent>> =
        eventDao.getAll(deviceId)

    fun getUnread(deviceId: String): LiveData<List<CompanionEvent>> =
        eventDao.getUnread(deviceId)

    suspend fun markRead(id: Long) = eventDao.markRead(id)

    suspend fun markAllRead(deviceId: String) = eventDao.markAllRead(deviceId)

    suspend fun postCompanionEvent(type: String, title: String, message: String) {
        val event = CompanionEvent(
            deviceId = securePrefs.deviceId,
            type     = type,
            title    = title,
            message  = message
        )
        eventDao.insert(event)
        runCatching { firebaseRepository.pushCompanionEvent(event) }
        showNotification(title, message)
    }

    // ── Habit Logs ────────────────────────────────────────────────────

    fun getAllHabitLogs(deviceId: String): LiveData<List<HabitLog>> =
        habitLogDao.getAll(deviceId)

    suspend fun getTodayHabitLog(deviceId: String): HabitLog? =
        habitLogDao.getForDate(deviceId, DateUtils.today())

    suspend fun saveHabitLog(log: HabitLog) {
        habitLogDao.insert(log)
        runCatching { firebaseRepository.pushHabitLog(log) }
    }

    // ── Smart Suggestions ─────────────────────────────────────────────

    suspend fun generateDailySummary(deviceId: String, screenTimeMs: Long) {
        val hours = screenTimeMs / MS_PER_HOUR
        val message = when {
            hours >= 6 -> "You've used your phone for over ${hours}h today. Consider a digital detox!"
            hours >= 3 -> "About ${hours}h of screen time today. You're on track."
            else -> "Low screen time today (${hours}h). Great job!"
        }
        postCompanionEvent("SUMMARY", "Daily Screen-Time Summary", message)
    }

    // ── Notification helper ───────────────────────────────────────────

    private fun showNotification(title: String, message: String) {
        if (!securePrefs.companionNotificationsEnabled) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, BumbleBeeApp.CHANNEL_COMPANION)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_companion)
            .setAutoCancel(true)
            .build()
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        private const val MS_PER_HOUR = 3_600_000L
    }
}
