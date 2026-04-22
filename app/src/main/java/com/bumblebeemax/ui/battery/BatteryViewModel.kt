package com.bumblebeemax.ui.battery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumblebeemax.data.model.BatteryInfo
import com.bumblebeemax.data.repository.BatteryRepository
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(
    private val repository: BatteryRepository,
    private val securePrefs: SecurePrefs
) : ViewModel() {

    val latestBattery: LiveData<BatteryInfo?> = repository.getLatest(securePrefs.deviceId)

    private val _history = MutableLiveData<List<BatteryInfo>>(emptyList())
    val history: LiveData<List<BatteryInfo>> get() = _history

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _history.value = repository.getRecent(securePrefs.deviceId, 48)
        }
    }
}
