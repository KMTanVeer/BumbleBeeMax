package com.bumblebeemax.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumblebeemax.data.repository.GeofenceRepository
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securePrefs: SecurePrefs,
    private val geofenceRepository: GeofenceRepository
) : ViewModel() {

    val deviceLabel: String get() = securePrefs.deviceLabel
    val ownerName: String   get() = securePrefs.ownerName

    var geofencingEnabled: Boolean
        get() = securePrefs.geofencingEnabled
        set(value) {
            securePrefs.geofencingEnabled = value
            if (value) viewModelScope.launch { geofenceRepository.reloadAllGeofences() }
        }

    var locationIntervalSeconds: Int
        get() = securePrefs.locationIntervalSeconds
        set(value) { securePrefs.locationIntervalSeconds = value }

    var companionNotificationsEnabled: Boolean
        get() = securePrefs.companionNotificationsEnabled
        set(value) { securePrefs.companionNotificationsEnabled = value }

    fun saveProfile(label: String, name: String) {
        securePrefs.deviceLabel = label
        securePrefs.ownerName   = name
    }
}
