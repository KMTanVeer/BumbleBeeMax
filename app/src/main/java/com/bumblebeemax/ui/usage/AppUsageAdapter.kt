package com.bumblebeemax.ui.usage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumblebeemax.data.model.AppUsageInfo
import com.bumblebeemax.databinding.ItemAppUsageBinding
import com.bumblebeemax.util.DateUtils

class AppUsageAdapter : ListAdapter<AppUsageInfo, AppUsageAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemAppUsageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(info: AppUsageInfo) {
            binding.tvAppName.text     = info.appName
            binding.tvDuration.text    = DateUtils.formatDuration(info.usageDurationMs)
            binding.tvLastUsed.text    = "Last used: ${DateUtils.formatTimestamp(info.lastUsedTimestamp)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppUsageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<AppUsageInfo>() {
            override fun areItemsTheSame(a: AppUsageInfo, b: AppUsageInfo) = a.id == b.id
            override fun areContentsTheSame(a: AppUsageInfo, b: AppUsageInfo) = a == b
        }
    }
}
