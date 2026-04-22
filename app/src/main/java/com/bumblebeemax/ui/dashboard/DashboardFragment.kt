package com.bumblebeemax.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumblebeemax.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDeviceLabel.text = viewModel.deviceLabel

        viewModel.myBattery.observe(viewLifecycleOwner) { info ->
            if (info != null) {
                binding.tvBatteryPercent.text = "${info.percentage}%"
                binding.tvBatteryStatus.text  = if (info.isCharging) "Charging (${info.chargingType})" else "Not Charging"
                binding.tvBatteryHealth.text  = "Health: ${info.health}"
                binding.batteryProgressBar.progress = info.percentage
            }
        }

        viewModel.myLocation.observe(viewLifecycleOwner) { info ->
            if (info != null) {
                binding.tvLocation.text = if (info.address.isNotBlank()) info.address
                    else "%.5f, %.5f".format(info.latitude, info.longitude)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
