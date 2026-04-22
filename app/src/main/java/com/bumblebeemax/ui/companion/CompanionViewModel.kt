package com.bumblebeemax.ui.companion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumblebeemax.data.model.CompanionEvent
import com.bumblebeemax.data.model.HabitLog
import com.bumblebeemax.data.repository.CompanionRepository
import com.bumblebeemax.util.DateUtils
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanionViewModel @Inject constructor(
    private val repository: CompanionRepository,
    private val securePrefs: SecurePrefs
) : ViewModel() {

    val events: LiveData<List<CompanionEvent>> = repository.getAll(securePrefs.deviceId)
    val unreadEvents: LiveData<List<CompanionEvent>> = repository.getUnread(securePrefs.deviceId)
    val habitLogs: LiveData<List<HabitLog>> = repository.getAllHabitLogs(securePrefs.deviceId)

    private val _todayLog = MutableLiveData<HabitLog?>()
    val todayLog: LiveData<HabitLog?> get() = _todayLog

    init {
        loadTodayLog()
    }

    fun loadTodayLog() {
        viewModelScope.launch {
            _todayLog.value = repository.getTodayHabitLog(securePrefs.deviceId)
        }
    }

    fun saveMoodAndHabit(mood: Int, moodLabel: String, note: String) {
        viewModelScope.launch {
            val log = HabitLog(
                deviceId  = securePrefs.deviceId,
                date      = DateUtils.today(),
                mood      = mood,
                moodLabel = moodLabel,
                habitNote = note
            )
            repository.saveHabitLog(log)
            _todayLog.value = log
        }
    }

    fun addReminder(title: String, message: String) {
        viewModelScope.launch {
            repository.postCompanionEvent("REMINDER", title, message)
        }
    }

    fun markRead(id: Long) {
        viewModelScope.launch { repository.markRead(id) }
    }

    fun markAllRead() {
        viewModelScope.launch { repository.markAllRead(securePrefs.deviceId) }
    }

    fun requestDailySummary(screenTimeMs: Long) {
        viewModelScope.launch {
            repository.generateDailySummary(securePrefs.deviceId, screenTimeMs)
        }
    }
}
