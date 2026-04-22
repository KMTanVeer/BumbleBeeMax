package com.bumblebeemax.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumblebeemax.data.model.GeofenceZone
import com.bumblebeemax.data.model.LocationInfo
import com.bumblebeemax.data.repository.GeofenceRepository
import com.bumblebeemax.data.repository.LocationRepository
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val geofenceRepository: GeofenceRepository,
    private val securePrefs: SecurePrefs
) : ViewModel() {

    val latestLocation: LiveData<LocationInfo?> = locationRepository.getLatest(securePrefs.deviceId)

    val geofenceZones: LiveData<List<GeofenceZone>> = geofenceRepository.getAllZones()

    fun addGeofenceZone(
        name: String,
        lat: Double,
        lng: Double,
        radiusMeters: Float,
        alertEnter: Boolean,
        alertExit: Boolean
    ) {
        viewModelScope.launch {
            val zone = GeofenceZone(
                zoneId        = "zone_${UUID.randomUUID()}",
                name          = name,
                latitude      = lat,
                longitude     = lng,
                radiusMeters  = radiusMeters,
                alertOnEnter  = alertEnter,
                alertOnExit   = alertExit
            )
            geofenceRepository.addZone(zone)
        }
    }

    fun deleteZone(zone: GeofenceZone) {
        viewModelScope.launch {
            geofenceRepository.deleteZone(zone)
        }
    }

    fun reloadGeofences() {
        viewModelScope.launch {
            geofenceRepository.reloadAllGeofences()
        }
    }
}
