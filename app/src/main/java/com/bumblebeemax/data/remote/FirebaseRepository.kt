package com.bumblebeemax.data.remote

import com.bumblebeemax.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ── Device Profile ────────────────────────────────────────────────

    suspend fun saveDeviceProfile(profile: DeviceProfile) {
        firestore.collection("devices")
            .document(profile.deviceId)
            .set(profile.toMap(), SetOptions.merge())
            .await()
    }

    suspend fun getDeviceProfiles(): List<DeviceProfile> {
        return firestore.collection("devices")
            .get().await()
            .documents
            .mapNotNull { it.toObject(DeviceProfile::class.java) }
    }

    // ── Battery ───────────────────────────────────────────────────────

    suspend fun pushBattery(info: BatteryInfo) {
        firestore.collection("devices")
            .document(info.deviceId)
            .collection("battery")
            .add(info.toMap())
            .await()
        // keep latest snapshot at top-level for quick read
        firestore.collection("devices")
            .document(info.deviceId)
            .set(mapOf("latestBattery" to info.toMap()), SetOptions.merge())
            .await()
    }

    // ── Location ──────────────────────────────────────────────────────

    suspend fun pushLocation(info: LocationInfo) {
        firestore.collection("devices")
            .document(info.deviceId)
            .collection("locations")
            .add(info.toMap())
            .await()
        firestore.collection("devices")
            .document(info.deviceId)
            .set(mapOf("latestLocation" to info.toMap()), SetOptions.merge())
            .await()
    }

    // ── App Usage ─────────────────────────────────────────────────────

    suspend fun pushUsage(deviceId: String, date: String, list: List<AppUsageInfo>) {
        val batch = firestore.batch()
        val colRef = firestore.collection("devices")
            .document(deviceId)
            .collection("usage")
        list.forEach { item ->
            val doc = colRef.document("${date}_${item.packageName}")
            batch.set(doc, item.toMap(), SetOptions.merge())
        }
        batch.commit().await()
    }

    // ── Geofence Events ───────────────────────────────────────────────

    suspend fun pushGeofenceEvent(event: GeofenceEvent) {
        firestore.collection("geofence_events")
            .add(event.toMap())
            .await()
    }

    // ── Companion Events ──────────────────────────────────────────────

    suspend fun pushCompanionEvent(event: CompanionEvent) {
        firestore.collection("companion_events")
            .add(event.toMap())
            .await()
    }

    // ── Habit Logs ────────────────────────────────────────────────────

    suspend fun pushHabitLog(log: HabitLog) {
        firestore.collection("devices")
            .document(log.deviceId)
            .collection("habit_logs")
            .document(log.date)
            .set(log.toMap(), SetOptions.merge())
            .await()
    }
}

// ── Extension helpers for Firestore serialisation ─────────────────────────

private fun DeviceProfile.toMap() = mapOf(
    "deviceId" to deviceId, "label" to label, "fcmToken" to fcmToken,
    "ownerName" to ownerName, "lastSeen" to lastSeen, "isOnline" to isOnline
)

private fun BatteryInfo.toMap() = mapOf(
    "deviceId" to deviceId, "deviceLabel" to deviceLabel,
    "percentage" to percentage, "isCharging" to isCharging,
    "chargingType" to chargingType, "health" to health,
    "temperature" to temperature, "voltage" to voltage, "timestamp" to timestamp
)

private fun LocationInfo.toMap() = mapOf(
    "deviceId" to deviceId, "deviceLabel" to deviceLabel,
    "latitude" to latitude, "longitude" to longitude, "accuracy" to accuracy,
    "altitude" to altitude, "speed" to speed, "address" to address,
    "timestamp" to timestamp
)

private fun AppUsageInfo.toMap() = mapOf(
    "deviceId" to deviceId, "deviceLabel" to deviceLabel,
    "packageName" to packageName, "appName" to appName,
    "usageDurationMs" to usageDurationMs, "lastUsedTimestamp" to lastUsedTimestamp,
    "date" to date, "timestamp" to timestamp
)

private fun GeofenceEvent.toMap() = mapOf(
    "zoneId" to zoneId, "zoneName" to zoneName,
    "deviceId" to deviceId, "deviceLabel" to deviceLabel,
    "transition" to transition, "timestamp" to timestamp
)

private fun CompanionEvent.toMap() = mapOf(
    "deviceId" to deviceId, "type" to type,
    "title" to title, "message" to message,
    "isRead" to isRead, "timestamp" to timestamp
)

private fun HabitLog.toMap() = mapOf(
    "deviceId" to deviceId, "date" to date, "mood" to mood,
    "moodLabel" to moodLabel, "habitNote" to habitNote,
    "screenTimeMs" to screenTimeMs, "stepCount" to stepCount, "timestamp" to timestamp
)
