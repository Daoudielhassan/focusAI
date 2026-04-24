package com.focus.mob.ui.onboarding
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.focus.mob.databinding.ActivityNiveauDistractionBinding

class NiveauDistractionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNiveauDistractionBinding
    private lateinit var cards: Array<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNiveauDistractionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cards = arrayOf(binding.cardZen, binding.cardMoyen, binding.cardDistrait)

        // Default selection
        selectCard(binding.cardMoyen)

        cards.forEach { card -> card.setOnClickListener { selectCard(it) } }

        binding.btnBack.setOnClickListener { finish() }
        binding.tvPasser.setOnClickListener { navigateToNext() }
        binding.btnContinuer.setOnClickListener { navigateToNext() }
    }

    private fun selectCard(selectedCard: View) {
        cards.forEach { it.isSelected = (it == selectedCard) }
    }

    private fun navigateToNext() {
        startActivity(Intent(this, AutorisationsActivity::class.java))
        fadeTransition()
    }
}
