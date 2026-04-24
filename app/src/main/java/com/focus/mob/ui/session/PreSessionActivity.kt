package com.focus.mob.ui.session
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.focus.mob.databinding.ActivityPreSessionBinding

class PreSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreSessionBinding
    private var selectedDurationMs = 30 * 60 * 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tv15 = binding.tvDuration15
        val tv30 = binding.tvDuration30
        val tv60 = binding.tvDuration60

        val initialDuration = intent.getIntExtra("selected_duration", 30)
        updateDurationSelection(initialDuration, tv15, tv30, tv60)

        tv15.setOnClickListener { updateDurationSelection(15, tv15, tv30, tv60) }
        tv30.setOnClickListener { updateDurationSelection(30, tv15, tv30, tv60) }
        tv60.setOnClickListener { updateDurationSelection(60, tv15, tv30, tv60) }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnStartFocus.setOnClickListener {
            startActivity(Intent(this, SessionImmersiveActivity::class.java).apply {
                putExtra("DURATION_MS", selectedDurationMs)
            })
            fadeTransition()
        }
    }

    private fun updateDurationSelection(minutes: Int, t15: TextView, t30: TextView, t60: TextView) {
        selectedDurationMs = minutes * 60 * 1000L

        listOf(t15, t30, t60).forEach {
            it.setBackgroundResource(R.drawable.bg_surface_dark_rounded)
            it.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_dark))
            it.setTypeface(null, Typeface.NORMAL)
        }

        val selected = when (minutes) {
            15 -> t15; 60 -> t60; else -> t30
        }
        selected.setBackgroundResource(R.drawable.bg_duration_chip_active)
        selected.setTextColor(ContextCompat.getColor(this, R.color.background_dark))
        selected.setTypeface(null, Typeface.BOLD)
    }
}
