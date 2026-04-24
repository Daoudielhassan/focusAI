package com.focus.mob.ui.session
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.focus.mob.data.AppDatabase
import com.focus.mob.data.repository.AuthRepository
import com.focus.mob.data.repository.SessionRepository
import com.focus.mob.databinding.ActivityFinSessionBinding
import com.focus.mob.ui.viewmodel.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinSessionBinding
    private var selectedMood = "Neutral"
    private var sessionDurationMs = 25 * 60 * 1000L

    private val sessionViewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionDurationMs = intent.getLongExtra("DURATION_MS", 25 * 60 * 1000L)

        binding.btnEmoji1.setOnClickListener { selectEmoji(it as android.widget.FrameLayout, "Bad") }
        binding.btnEmoji2.setOnClickListener { selectEmoji(it as android.widget.FrameLayout, "Neutral") }
        binding.btnEmoji3.setOnClickListener { selectEmoji(it as android.widget.FrameLayout, "Great") }

        binding.btnInsights.setOnClickListener {
            saveSessionToDb()
            startActivity(Intent(this, StatsActivity::class.java))
            fadeTransition()
        }

        binding.btnHome.setOnClickListener {
            saveSessionToDb()
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            fadeTransition()
        }
    }

    private fun selectEmoji(btn: android.widget.FrameLayout, mood: String) {
        resetEmojis()
        btn.setBackgroundResource(R.drawable.bg_emoji_selected)
        selectedMood = mood
    }

    private fun resetEmojis() {
        binding.btnEmoji1.setBackgroundResource(R.drawable.bg_emoji_unselected)
        binding.btnEmoji2.setBackgroundResource(R.drawable.bg_emoji_unselected)
        binding.btnEmoji3.setBackgroundResource(R.drawable.bg_emoji_unselected)
    }

    private fun saveSessionToDb() {
        val minutes = (sessionDurationMs / (1000 * 60)).toInt()
        sessionViewModel.saveSession(minutes, selectedMood)
    }
}
