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
import com.focus.mob.databinding.ActivityObjectifPrincipalBinding

class ObjectifPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityObjectifPrincipalBinding
    private lateinit var cards: Array<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectifPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cards = arrayOf(
            binding.cardStudy, binding.cardWork,
            binding.cardRead, binding.cardCreate, binding.cardOther
        )

        cards.forEach { card -> card.setOnClickListener { selectCard(it) } }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnContinuer.setOnClickListener {
            startActivity(Intent(this, NiveauDistractionActivity::class.java))
            fadeTransition()
        }
    }

    private fun selectCard(selectedCard: View) {
        cards.forEach { it.isSelected = (it == selectedCard) }
    }
}
