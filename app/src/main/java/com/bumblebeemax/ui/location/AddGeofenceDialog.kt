package com.bumblebeemax.ui.location

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumblebeemax.databinding.DialogAddGeofenceBinding

class AddGeofenceDialog(
    private val onAdd: (name: String, lat: Double, lng: Double, radius: Float, enter: Boolean, exit: Boolean) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddGeofenceBinding.inflate(layoutInflater)

        return AlertDialog.Builder(requireContext())
            .setTitle("Add Geofence Zone")
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ ->
                val name   = binding.etZoneName.text.toString().trim()
                val latStr = binding.etLatitude.text.toString().trim()
                val lngStr = binding.etLongitude.text.toString().trim()
                val radStr = binding.etRadius.text.toString().trim()

                if (name.isEmpty() || latStr.isEmpty() || lngStr.isEmpty() || radStr.isEmpty()) {
                    Toast.makeText(requireContext(), "All fields required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                runCatching {
                    val lat    = latStr.toDouble()
                    val lng    = lngStr.toDouble()
                    val radius = radStr.toFloat()
                    onAdd(name, lat, lng, radius, binding.cbEnter.isChecked, binding.cbExit.isChecked)
                }.onFailure {
                    Toast.makeText(requireContext(), "Invalid coordinates", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
