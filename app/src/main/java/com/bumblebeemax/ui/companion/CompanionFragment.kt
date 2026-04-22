package com.bumblebeemax.ui.companion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumblebeemax.data.model.HabitLog
import com.bumblebeemax.databinding.FragmentCompanionBinding
import com.bumblebeemax.util.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CompanionFragment : Fragment() {

    private var _binding: FragmentCompanionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CompanionViewModel by viewModels()
    private lateinit var eventsAdapter: CompanionEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsAdapter = CompanionEventAdapter(onRead = { viewModel.markRead(it.id) })
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = eventsAdapter

        viewModel.events.observe(viewLifecycleOwner) { eventsAdapter.submitList(it) }

        viewModel.todayLog.observe(viewLifecycleOwner) { log ->
            if (log != null) {
                binding.tvTodayMood.text = "Today's mood: ${log.moodLabel} (${log.mood}/5)"
                binding.etHabitNote.setText(log.habitNote)
            }
        }

        // Mood slider
        binding.moodSlider.addOnChangeListener { _, value, _ ->
            binding.tvMoodValue.text = value.toInt().toString()
        }

        binding.btnSaveMood.setOnClickListener {
            val mood  = binding.moodSlider.value.toInt()
            val label = moodLabel(mood)
            val note  = binding.etHabitNote.text.toString().trim()
            viewModel.saveMoodAndHabit(mood, label, note)
            Toast.makeText(requireContext(), "Habit log saved!", Toast.LENGTH_SHORT).show()
        }

        binding.btnAddReminder.setOnClickListener {
            val title   = binding.etReminderTitle.text.toString().trim()
            val message = binding.etReminderMessage.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.addReminder(title, message)
                binding.etReminderTitle.text?.clear()
                binding.etReminderMessage.text?.clear()
                Toast.makeText(requireContext(), "Reminder added", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnMarkAllRead.setOnClickListener { viewModel.markAllRead() }
    }

    private fun moodLabel(mood: Int) = when (mood) {
        1 -> "Bad"
        2 -> "Sad"
        3 -> "Okay"
        4 -> "Good"
        5 -> "Great"
        else -> "Okay"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
