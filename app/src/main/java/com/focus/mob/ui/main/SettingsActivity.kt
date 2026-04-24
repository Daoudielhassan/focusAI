package com.focus.mob.ui.main
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.focus.mob.data.repository.AuthRepository
import com.focus.mob.ui.viewmodel.AuthViewModel
import com.focus.mob.ui.viewmodel.SessionViewModel
import androidx.lifecycle.lifecycleScope
import com.focus.mob.data.AppDatabase
import com.focus.mob.data.repository.SessionRepository
import com.focus.mob.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val sessionViewModel: SessionViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationUtils.setupBottomNavigation(this, NavigationUtils.Tab.SETTINGS)

        val prefs = getSharedPreferences("LuminaPrefs", MODE_PRIVATE)

        // Load Preferences
        binding.switchNotifications.isChecked = prefs.getBoolean("smart_notifications", true)
        binding.switchMusic.isChecked = prefs.getBoolean("auto_music", false)
        val goalProgress = prefs.getInt("daily_goal", 3)
        binding.seekbarGoal.progress = goalProgress
        binding.tvGoalVal.text = "${goalProgress + 1} hrs"

        // Listeners for Switches
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("smart_notifications", isChecked).apply()
        }
        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("auto_music", isChecked).apply()
        }

        // Listener for SeekBar
        binding.seekbarGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.tvGoalVal.text = "${progress + 1} hrs"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                prefs.edit().putInt("daily_goal", seekBar.progress).apply()
            }
        })

        // Listener for Reset
        binding.btnResetData.setOnClickListener {
            sessionViewModel.resetData()
            Toast.makeText(this@SettingsActivity, getString(R.string.settings_reset_success), Toast.LENGTH_SHORT).show()
        }

        // Listener for Logout
        binding.btnLogout.setOnClickListener {
            authViewModel.signOut()
            startActivity(Intent(this, WelcomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            fadeTransition()
        }
    }
}
