package com.bumblebeemax.ui.companion

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumblebeemax.data.model.CompanionEvent
import com.bumblebeemax.databinding.ItemCompanionEventBinding
import com.bumblebeemax.util.DateUtils

class CompanionEventAdapter(
    private val onRead: (CompanionEvent) -> Unit
) : ListAdapter<CompanionEvent, CompanionEventAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemCompanionEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: CompanionEvent) {
            binding.tvEventTitle.text   = event.title
            binding.tvEventMessage.text = event.message
            binding.tvEventType.text    = event.type
            binding.tvEventTime.text    = DateUtils.formatTimestamp(event.timestamp)

            val style = if (event.isRead) Typeface.NORMAL else Typeface.BOLD
            binding.tvEventTitle.setTypeface(null, style)

            binding.root.setOnClickListener { if (!event.isRead) onRead(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCompanionEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<CompanionEvent>() {
            override fun areItemsTheSame(a: CompanionEvent, b: CompanionEvent) = a.id == b.id
            override fun areContentsTheSame(a: CompanionEvent, b: CompanionEvent) = a == b
        }
    }
}
