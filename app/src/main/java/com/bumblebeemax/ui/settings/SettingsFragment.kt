package com.bumblebeemax.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumblebeemax.databinding.FragmentSettingsBinding
import com.bumblebeemax.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate saved values
        binding.etDeviceLabel.setText(viewModel.deviceLabel)
        binding.etOwnerName.setText(viewModel.ownerName)
        binding.switchGeofencing.isChecked = viewModel.geofencingEnabled
        binding.switchCompanionNotif.isChecked = viewModel.companionNotificationsEnabled
        binding.sliderLocationInterval.value = viewModel.locationIntervalSeconds.toFloat()

        binding.btnSaveProfile.setOnClickListener {
            val label = binding.etDeviceLabel.text.toString().trim()
            val name  = binding.etOwnerName.text.toString().trim()
            if (label.isNotEmpty()) {
                viewModel.saveProfile(label, name)
                Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show()
            }
        }

        binding.switchGeofencing.setOnCheckedChangeListener { _, checked ->
            viewModel.geofencingEnabled = checked
        }

        binding.switchCompanionNotif.setOnCheckedChangeListener { _, checked ->
            viewModel.companionNotificationsEnabled = checked
        }

        binding.sliderLocationInterval.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.locationIntervalSeconds = value.toInt()
        }

        // Permission status row
        updatePermissionStatuses()

        binding.btnOpenUsageAccess.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        binding.btnOpenLocationSettings.setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatuses()
    }

    private fun updatePermissionStatuses() {
        val ctx = requireContext()
        binding.tvPermLocation.text =
            if (PermissionUtils.hasLocationPermission(ctx)) "✅ Location" else "❌ Location"
        binding.tvPermBgLocation.text =
            if (PermissionUtils.hasBackgroundLocationPermission(ctx)) "✅ Background Location" else "❌ Background Location"
        binding.tvPermUsage.text =
            if (PermissionUtils.hasUsageStatsPermission(ctx)) "✅ Usage Stats" else "❌ Usage Stats"
        binding.tvPermNotification.text =
            if (PermissionUtils.hasNotificationPermission(ctx)) "✅ Notifications" else "❌ Notifications"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
