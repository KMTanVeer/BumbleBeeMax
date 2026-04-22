package com.bumblebeemax.ui.usage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumblebeemax.data.local.dao.AppUsageDao
import com.bumblebeemax.data.model.AppUsageInfo
import com.bumblebeemax.data.repository.UsageRepository
import com.bumblebeemax.util.DateUtils
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsageViewModel @Inject constructor(
    private val repository: UsageRepository,
    private val securePrefs: SecurePrefs
) : ViewModel() {

    private val _selectedDate = MutableLiveData(DateUtils.today())
    val selectedDate: LiveData<String> get() = _selectedDate

    val appList: LiveData<List<AppUsageInfo>> =
        repository.getForDay(securePrefs.deviceId, DateUtils.today())

    private val _totalScreenTime = MutableLiveData(0L)
    val totalScreenTimeMs: LiveData<Long> get() = _totalScreenTime

    private val _weeklyTotals = MutableLiveData<List<AppUsageDao.DailyTotal>>(emptyList())
    val weeklyTotals: LiveData<List<AppUsageDao.DailyTotal>> get() = _weeklyTotals

    init {
        loadTotals()
    }

    fun loadTotals() {
        viewModelScope.launch {
            _totalScreenTime.value = repository.getTotalScreenTimeMs(
                securePrefs.deviceId, DateUtils.today()
            )
            _weeklyTotals.value = repository.getWeeklyTotals(
                securePrefs.deviceId, DateUtils.daysAgo(6)
            )
        }
    }
}
