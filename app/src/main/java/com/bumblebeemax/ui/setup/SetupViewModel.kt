package com.bumblebeemax.ui.setup

import androidx.lifecycle.ViewModel
import com.bumblebeemax.data.model.DeviceProfile
import com.bumblebeemax.data.remote.FirebaseRepository
import com.bumblebeemax.util.SecurePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val securePrefs: SecurePrefs,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun completeSetup(label: String, ownerName: String) {
        val deviceId = UUID.randomUUID().toString()
        securePrefs.deviceId       = deviceId
        securePrefs.deviceLabel    = label
        securePrefs.ownerName      = ownerName
        securePrefs.isSetupComplete = true
        securePrefs.consentGiven   = true

        scope.launch {
            runCatching {
                firebaseRepository.saveDeviceProfile(
                    DeviceProfile(
                        deviceId  = deviceId,
                        label     = label,
                        ownerName = ownerName,
                        fcmToken  = securePrefs.fcmToken,
                        isOnline  = true,
                        lastSeen  = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
