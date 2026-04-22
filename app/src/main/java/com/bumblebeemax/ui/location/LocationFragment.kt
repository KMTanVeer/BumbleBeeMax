package com.bumblebeemax.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumblebeemax.databinding.FragmentLocationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocationViewModel by viewModels()
    private lateinit var adapter: GeofenceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GeofenceAdapter(onDelete = { viewModel.deleteZone(it) })
        binding.rvGeofences.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGeofences.adapter = adapter

        viewModel.latestLocation.observe(viewLifecycleOwner) { info ->
            if (info != null) {
                binding.tvCurrentLocation.text =
                    if (info.address.isNotBlank()) info.address
                    else "%.5f, %.5f".format(info.latitude, info.longitude)
                binding.tvAccuracy.text = "Accuracy: ±${info.accuracy.toInt()}m"
            }
        }

        viewModel.geofenceZones.observe(viewLifecycleOwner) { zones ->
            adapter.submitList(zones)
        }

        binding.btnAddGeofence.setOnClickListener {
            showAddGeofenceDialog()
        }
    }

    private fun showAddGeofenceDialog() {
        AddGeofenceDialog { name, lat, lng, radius, enter, exit ->
            viewModel.addGeofenceZone(name, lat, lng, radius, enter, exit)
            Toast.makeText(requireContext(), "Geofence '$name' added", Toast.LENGTH_SHORT).show()
        }.show(childFragmentManager, "AddGeofence")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
