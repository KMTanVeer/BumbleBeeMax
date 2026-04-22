package com.bumblebeemax.ui.setup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumblebeemax.MainActivity
import com.bumblebeemax.databinding.ActivitySetupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private val viewModel: SetupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSetupMyPhone.setOnClickListener {
            saveAndProceed("My Phone", binding.etMyName.text.toString().trim())
        }

        binding.btnSetupWifesPhone.setOnClickListener {
            saveAndProceed("Wife's Phone", binding.etWifeName.text.toString().trim())
        }
    }

    private fun saveAndProceed(label: String, ownerName: String) {
        if (ownerName.isEmpty()) {
            Toast.makeText(this, "Enter owner name", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.completeSetup(label, ownerName)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
