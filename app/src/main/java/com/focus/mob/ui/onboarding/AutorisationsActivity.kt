package com.focus.mob.ui.onboarding
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.focus.mob.databinding.ActivityAutorisationsBinding

class AutorisationsActivity : AppCompatActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val prefs = getSharedPreferences("LuminaPrefs", MODE_PRIVATE)
            if (!granted) {
                // User denied: disable the setting to stay consistent
                prefs.edit().putBoolean("smart_notifications", false).apply()
                binding.switchNotif.isChecked = false
            }
        }

    private lateinit var binding: ActivityAutorisationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutorisationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("LuminaPrefs", MODE_PRIVATE)

        // Load saved states
        binding.switchNotif.isChecked = prefs.getBoolean("smart_notifications", true)
        binding.switchMusic.isChecked = prefs.getBoolean("auto_music", false)

        binding.switchNotif.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("smart_notifications", isChecked).apply()
            if (isChecked) requestPostNotificationsPermission()
        }
        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("auto_music", isChecked).apply()
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnContinuer.setOnClickListener {
            // Request permission if notifications are enabled and not yet granted
            if (binding.switchNotif.isChecked) requestPostNotificationsPermission()
            prefs.edit().putBoolean("onboarding_done", true).apply()
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            fadeTransition()
        }
    }

    private fun requestPostNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
