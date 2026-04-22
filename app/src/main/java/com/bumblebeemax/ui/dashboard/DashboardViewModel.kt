package com.bumblebeemax.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.bumblebeemax.data.model.BatteryInfo
import com.bumblebeemax.data.model.LocationInfo
import com.bumblebeemax.data.repository.BatteryRepository
import com.bumblebeemax.data.repository.LocationRepository
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val batteryRepository: BatteryRepository,
    private val locationRepository: LocationRepository,
    private val securePrefs: SecurePrefs
) : ViewModel() {

    val deviceId: String get() = securePrefs.deviceId
    val deviceLabel: String get() = securePrefs.deviceLabel

    val myBattery: LiveData<BatteryInfo?> = batteryRepository.getLatest(securePrefs.deviceId)
    val myLocation: LiveData<LocationInfo?> = locationRepository.getLatest(securePrefs.deviceId)
}
