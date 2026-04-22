package com.bumblebeemax.data.repository

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.bumblebeemax.data.local.dao.GeofenceDao
import com.bumblebeemax.data.model.GeofenceZone
import com.bumblebeemax.receiver.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: GeofenceDao
) {
    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            context, 0,
            Intent(context, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun getAllZones(): LiveData<List<GeofenceZone>> = dao.getAll()

    suspend fun addZone(zone: GeofenceZone) {
        dao.insert(zone)
        registerGeofence(zone)
    }

    suspend fun updateZone(zone: GeofenceZone) {
        dao.update(zone)
        reloadAllGeofences()
    }

    suspend fun deleteZone(zone: GeofenceZone) {
        dao.delete(zone)
        geofencingClient.removeGeofences(listOf(zone.zoneId)).await()
    }

    @SuppressLint("MissingPermission")
    suspend fun reloadAllGeofences() {
        val zones = dao.getActiveZones()
        if (zones.isEmpty()) return

        geofencingClient.removeGeofences(geofencePendingIntent).await()

        val geofences = zones.map { zone ->
            Geofence.Builder()
                .setRequestId(zone.zoneId)
                .setCircularRegion(zone.latitude, zone.longitude, zone.radiusMeters)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(buildTransitionMask(zone))
                .build()
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent).await()
    }

    @SuppressLint("MissingPermission")
    private suspend fun registerGeofence(zone: GeofenceZone) {
        val geofence = Geofence.Builder()
            .setRequestId(zone.zoneId)
            .setCircularRegion(zone.latitude, zone.longitude, zone.radiusMeters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(buildTransitionMask(zone))
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent).await()
    }

    private fun buildTransitionMask(zone: GeofenceZone): Int {
        var mask = 0
        if (zone.alertOnEnter) mask = mask or Geofence.GEOFENCE_TRANSITION_ENTER
        if (zone.alertOnExit)  mask = mask or Geofence.GEOFENCE_TRANSITION_EXIT
        return mask
    }
}
