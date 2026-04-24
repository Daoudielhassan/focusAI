package com.focus.mob.ui.onboarding
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.focus.mob.databinding.ActivityAutorisationsBinding

class AutorisationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAutorisationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("LuminaPrefs", MODE_PRIVATE)

        // Load saved states
        binding.switchNotif.isChecked = prefs.getBoolean("smart_notifications", true)
        binding.switchMusic.isChecked = prefs.getBoolean("auto_music", false)

        binding.switchNotif.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("smart_notifications", isChecked).apply()
        }
        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("auto_music", isChecked).apply()
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnContinuer.setOnClickListener {
            prefs.edit().putBoolean("onboarding_done", true).apply()
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            fadeTransition()
        }
    }
}
