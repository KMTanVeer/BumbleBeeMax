package com.bumblebeemax.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.bumblebeemax.BumbleBeeApp
import com.bumblebeemax.MainActivity
import com.bumblebeemax.R
import com.bumblebeemax.data.local.dao.LocationDao
import com.bumblebeemax.data.model.LocationInfo
import com.bumblebeemax.data.remote.FirebaseRepository
import com.bumblebeemax.util.SecurePrefs
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackingService : LifecycleService() {

    @Inject lateinit var locationDao: LocationDao
    @Inject lateinit var firebaseRepository: FirebaseRepository
    @Inject lateinit var securePrefs: SecurePrefs

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { loc ->
                lifecycleScope.launch { handleLocation(loc) }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient    = LocationServices.getGeofencingClient(this)
        startForeground(NOTIFICATION_ID, buildForegroundNotification())
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val intervalMs = securePrefs.locationIntervalSeconds * 1000L
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .setWaitForAccurateLocation(false)
            .build()

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private suspend fun handleLocation(loc: android.location.Location) {
        val address = resolveAddress(loc.latitude, loc.longitude)
        val info = LocationInfo(
            deviceId    = securePrefs.deviceId,
            deviceLabel = securePrefs.deviceLabel,
            latitude    = loc.latitude,
            longitude   = loc.longitude,
            accuracy    = loc.accuracy,
            altitude    = loc.altitude,
            speed       = loc.speed,
            address     = address
        )
        locationDao.insert(info)
        runCatching { firebaseRepository.pushLocation(info) }
    }

    private fun resolveAddress(lat: Double, lng: Double): String {
        return runCatching {
            val geocoder = Geocoder(this, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var result = ""
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    result = addresses.firstOrNull()?.getAddressLine(0) ?: ""
                }
                result
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                addresses?.firstOrNull()?.getAddressLine(0) ?: ""
            }
        }.getOrDefault("")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun buildForegroundNotification(): Notification {
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, BumbleBeeApp.CHANNEL_LOCATION)
            .setContentTitle("Location Tracking")
            .setContentText("Tracking device location…")
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent): IBinder? = super.onBind(intent)

    companion object {
        private const val NOTIFICATION_ID = 1002
    }
}
