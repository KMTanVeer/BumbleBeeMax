package com.bumblebeemax.ui.battery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumblebeemax.databinding.FragmentBatteryBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BatteryFragment : Fragment() {

    private var _binding: FragmentBatteryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BatteryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBatteryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.latestBattery.observe(viewLifecycleOwner) { info ->
            if (info != null) {
                binding.tvPercent.text       = "${info.percentage}%"
                binding.tvCharging.text      = if (info.isCharging) "⚡ Charging (${info.chargingType})" else "🔋 Discharging"
                binding.tvHealth.text        = "Health: ${info.health}"
                binding.tvTemperature.text   = "Temp: ${info.temperature}°C"
                binding.tvVoltage.text       = "Voltage: ${info.voltage} mV"
                binding.batteryProgress.progress = info.percentage
            }
        }

        viewModel.loadHistory()

        // Render battery history chart when history is ready
        // (history is loaded in ViewModel init; observe via a simple post-delay workaround)
        binding.root.post { renderChart() }
    }

    private fun renderChart() {
        val history = viewModel.history
        if (history.isEmpty()) return

        val entries = history.mapIndexed { idx, info ->
            Entry(idx.toFloat(), info.percentage.toFloat())
        }

        val dataSet = LineDataSet(entries, "Battery %").apply {
            setDrawFilled(true)
            lineWidth = 2f
            setDrawCircles(false)
        }

        with(binding.batteryChart) {
            data = LineData(dataSet)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            description.isEnabled = false
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
