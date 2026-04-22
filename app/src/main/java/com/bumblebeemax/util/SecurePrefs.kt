package com.bumblebeemax.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePrefs @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "bumblebee_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // ── Device Identity ───────────────────────────────────────────────

    var deviceId: String
        get() = prefs.getString(KEY_DEVICE_ID, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DEVICE_ID, value).apply()

    var deviceLabel: String
        get() = prefs.getString(KEY_DEVICE_LABEL, "My Phone") ?: "My Phone"
        set(value) = prefs.edit().putString(KEY_DEVICE_LABEL, value).apply()

    var ownerName: String
        get() = prefs.getString(KEY_OWNER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_OWNER_NAME, value).apply()

    // ── FCM ───────────────────────────────────────────────────────────

    var fcmToken: String
        get() = prefs.getString(KEY_FCM_TOKEN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_FCM_TOKEN, value).apply()

    // ── Setup flag ────────────────────────────────────────────────────

    var isSetupComplete: Boolean
        get() = prefs.getBoolean(KEY_SETUP_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_SETUP_DONE, value).apply()

    // ── Geofencing ────────────────────────────────────────────────────

    var geofencingEnabled: Boolean
        get() = prefs.getBoolean(KEY_GEOFENCE_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_GEOFENCE_ENABLED, value).apply()

    // ── Location interval ─────────────────────────────────────────────

    var locationIntervalSeconds: Int
        get() = prefs.getInt(KEY_LOCATION_INTERVAL, 30)
        set(value) = prefs.edit().putInt(KEY_LOCATION_INTERVAL, value).apply()

    // ── Companion notifications ───────────────────────────────────────

    var companionNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_COMPANION_NOTIF, true)
        set(value) = prefs.edit().putBoolean(KEY_COMPANION_NOTIF, value).apply()

    // ── Consent ───────────────────────────────────────────────────────

    var consentGiven: Boolean
        get() = prefs.getBoolean(KEY_CONSENT, false)
        set(value) = prefs.edit().putBoolean(KEY_CONSENT, value).apply()

    companion object {
        private const val KEY_DEVICE_ID        = "device_id"
        private const val KEY_DEVICE_LABEL     = "device_label"
        private const val KEY_OWNER_NAME       = "owner_name"
        private const val KEY_FCM_TOKEN        = "fcm_token"
        private const val KEY_SETUP_DONE       = "setup_done"
        private const val KEY_GEOFENCE_ENABLED = "geofence_enabled"
        private const val KEY_LOCATION_INTERVAL = "location_interval"
        private const val KEY_COMPANION_NOTIF  = "companion_notif"
        private const val KEY_CONSENT          = "consent_given"
    }
}
