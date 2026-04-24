package com.focus.mob.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.focus.mob.R
import com.focus.mob.databinding.ActivityHomeBinding
import com.focus.mob.ui.session.PreSessionActivity
import com.focus.mob.utils.NavigationUtils
import com.focus.mob.utils.fadeTransition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var selectedDuration = 30

    private val runningAnimators = mutableListOf<AnimatorSet>()
    private val runningObjectAnimators = mutableListOf<ObjectAnimator>()

    private val quotes = listOf(
        "\"La concentration est la racine de toute réussite.\"",
        "\"Un seul objectif à la fois. Puis un autre.\"",
        "\"Le flux commence quand on s'oublie soi-même.\"",
        "\"Chaque session compte, même les plus courtes.\"",
        "\"Soyez présent. Maintenant. Ici.\""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDurationSelection()
        setupCta()

        NavigationUtils.setupBottomNavigation(
            this,
            NavigationUtils.Tab.HOME
        )

        startEntranceAnimations()
        startOrbPulse()
        startEqualizerAnimation()
        rotateQuotes()
    }

    private fun setupDurationSelection() {
        binding.tvDuration15.setOnClickListener {
            updateSelection(15)
        }

        binding.tvDuration30.setOnClickListener {
            updateSelection(30)
        }

        binding.tvDuration60.setOnClickListener {
            updateSelection(60)
        }

        updateSelection(30, animate = false)
    }

    private fun setupCta() {
        binding.btnCommencer.setOnClickListener { button ->
            animatePress(button)

            button.postDelayed({
                val intent = Intent(this, PreSessionActivity::class.java).apply {
                    putExtra("selected_duration", selectedDuration)
                }

                startActivity(intent)
                fadeTransition()
            }, 180)
        }
    }

    private fun startEntranceAnimations() {
        val animatedViews = listOf(
            binding.topBar,
            binding.cardHero,
            binding.cardDuration,
            binding.cardToday,
            binding.cardAmbiance,
            binding.cardLumina,
            binding.btnCommencer
        )

        animatedViews.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 42f
            view.scaleX = 0.98f
            view.scaleY = 0.98f

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(index * 80L)
                .setDuration(560L)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        binding.tvStatusLabel.alpha = 0f
        binding.tvStatusLabel.translationY = -12f
        binding.tvStatusLabel.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(240L)
            .setDuration(520L)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    private fun startOrbPulse() {
        val ring = binding.viewOrbRingOuter

        val pulseScaleX = ObjectAnimator.ofFloat(ring, View.SCALE_X, 1f, 1.15f)
        val pulseScaleY = ObjectAnimator.ofFloat(ring, View.SCALE_Y, 1f, 1.15f)
        val pulseAlpha = ObjectAnimator.ofFloat(ring, View.ALPHA, 0.55f, 0.16f)

        listOf(pulseScaleX, pulseScaleY, pulseAlpha).forEach { animator ->
            animator.repeatMode = ValueAnimator.REVERSE
            animator.repeatCount = ValueAnimator.INFINITE
            animator.duration = 2200L
            animator.interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(pulseScaleX, pulseScaleY, pulseAlpha)
            start()
            runningAnimators.add(this)
        }

        val orbScaleX = ObjectAnimator.ofFloat(binding.cardOrb, View.SCALE_X, 1f, 1.04f)
        val orbScaleY = ObjectAnimator.ofFloat(binding.cardOrb, View.SCALE_Y, 1f, 1.04f)

        listOf(orbScaleX, orbScaleY).forEach { animator ->
            animator.repeatMode = ValueAnimator.REVERSE
            animator.repeatCount = ValueAnimator.INFINITE
            animator.duration = 3500L
            animator.interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(orbScaleX, orbScaleY)
            start()
            runningAnimators.add(this)
        }
    }

    private fun startEqualizerAnimation() {
        val bars = listOf(
            binding.eqBar1 to 420L,
            binding.eqBar2 to 620L,
            binding.eqBar3 to 360L,
            binding.eqBar4 to 720L,
            binding.eqBar5 to 480L
        )

        bars.forEach { (bar, duration) ->
            bar.pivotY = bar.height.toFloat()

            val animator = ObjectAnimator.ofFloat(bar, View.SCALE_Y, 0.75f, 2.35f).apply {
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                this.duration = duration
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }

            runningObjectAnimators.add(animator)
        }
    }

    private fun rotateQuotes() {
        lifecycleScope.launch {
            var index = 0

            while (true) {
                delay(6000L)

                index = (index + 1) % quotes.size

                binding.tvQuote.animate()
                    .alpha(0f)
                    .setDuration(360L)
                    .withEndAction {
                        binding.tvQuote.text = quotes[index]

                        binding.tvQuote.animate()
                            .alpha(1f)
                            .setDuration(360L)
                            .start()
                    }
                    .start()
            }
        }
    }

    private fun updateSelection(duration: Int, animate: Boolean = true) {
        selectedDuration = duration

        val allDurations = listOf(
            binding.tvDuration15,
            binding.tvDuration30,
            binding.tvDuration60
        )

        allDurations.forEach { item ->
            item.setBackgroundResource(R.drawable.bg_duration_chip_inactive)
            item.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_dark))
            item.typeface = Typeface.DEFAULT
        }

        val selectedView = when (duration) {
            15 -> binding.tvDuration15
            60 -> binding.tvDuration60
            else -> binding.tvDuration30
        }

        selectedView.setBackgroundResource(R.drawable.bg_duration_chip_active)
        selectedView.setTextColor(ContextCompat.getColor(this, R.color.background_dark))
        selectedView.typeface = Typeface.DEFAULT_BOLD

        if (animate) {
            animateSelection(selectedView)
        }
    }

    private fun animateSelection(view: TextView) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.08f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.08f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 250L
            interpolator = OvershootInterpolator()
            start()
        }
    }

    private fun animatePress(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.96f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.96f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 200L
            interpolator = OvershootInterpolator()
            start()
        }
    }

    override fun onDestroy() {
        runningAnimators.forEach { it.cancel() }
        runningObjectAnimators.forEach { it.cancel() }
        super.onDestroy()
    }
}