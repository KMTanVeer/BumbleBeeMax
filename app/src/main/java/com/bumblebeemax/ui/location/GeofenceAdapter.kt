package com.bumblebeemax.ui.location

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumblebeemax.data.model.GeofenceZone
import com.bumblebeemax.databinding.ItemGeofenceBinding

class GeofenceAdapter(
    private val onDelete: (GeofenceZone) -> Unit
) : ListAdapter<GeofenceZone, GeofenceAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemGeofenceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(zone: GeofenceZone) {
            binding.tvZoneName.text   = zone.name
            binding.tvZoneRadius.text = "Radius: ${zone.radiusMeters.toInt()}m"
            binding.tvZoneAlerts.text = buildString {
                if (zone.alertOnEnter) append("Enter ")
                if (zone.alertOnExit)  append("Exit")
            }
            binding.btnDelete.setOnClickListener { onDelete(zone) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGeofenceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<GeofenceZone>() {
            override fun areItemsTheSame(a: GeofenceZone, b: GeofenceZone) = a.zoneId == b.zoneId
            override fun areContentsTheSame(a: GeofenceZone, b: GeofenceZone) = a == b
        }
    }
}
