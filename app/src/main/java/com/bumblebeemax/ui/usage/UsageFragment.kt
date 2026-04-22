package com.bumblebeemax.ui.usage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumblebeemax.databinding.FragmentUsageBinding
import com.bumblebeemax.util.DateUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsageFragment : Fragment() {

    private var _binding: FragmentUsageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsageViewModel by viewModels()
    private lateinit var adapter: AppUsageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AppUsageAdapter()
        binding.rvApps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApps.adapter = adapter

        viewModel.appList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.take(20)) // show top 20 apps
        }

        viewModel.totalScreenTimeMs.observe(viewLifecycleOwner) { ms ->
            binding.tvTotalScreenTime.text = "Today: ${DateUtils.formatDuration(ms)}"
        }

        viewModel.weeklyTotals.observe(viewLifecycleOwner) { totals ->
            renderWeeklyChart(totals.map { it.date to it.usageDurationMs })
        }
    }

    private fun renderWeeklyChart(data: List<Pair<String, Long>>) {
        if (data.isEmpty()) return
        val entries = data.mapIndexed { i, (_, ms) ->
            BarEntry(i.toFloat(), ms / 3_600_000f) // hours
        }
        val dataSet = BarDataSet(entries, "Screen Time (hrs)").apply {
            setDrawValues(true)
        }
        with(binding.weeklyChart) {
            this.data = BarData(dataSet)
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
