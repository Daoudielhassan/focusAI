package com.focus.mob.ui.session

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.focus.mob.databinding.ActivitySessionImmersiveBinding
import com.focus.mob.ui.main.HomeActivity
import com.focus.mob.utils.fadeTransition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class SessionImmersiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionImmersiveBinding
    private val viewModel: SessionImmersiveViewModel by viewModels()

    private val equalizerAnimators = mutableListOf<ObjectAnimator>()

    private val motivationalPhrases = listOf(
        "Restez dans la zone...",
        "Chaque seconde compte.",
        "Votre concentration est votre superpouvoir.",
        "Ignorez les distractions.",
        "Vous êtes en train de progresser.",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionImmersiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeUiState()
        observeEvents()
        startBreathingAnimation()
        startGlowPulse()
        startEqualizerAnimation()
        rotateMotivationalText()
    }

    // ═══════════════════════════════════════
    // SETUP
    // ═══════════════════════════════════════

    private fun setupListeners() {
        binding.btnPause.setOnClickListener {
            viewModel.togglePause()
        }

        binding.btnEmergency.setOnClickListener {
            it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                viewModel.abandon()
            }.start()
        }
    }

    // ═══════════════════════════════════════
    // STATE OBSERVATION
    // ═══════════════════════════════════════

    private fun observeUiState() {
        var prevIsPaused: Boolean? = null

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    // ── Timer display
                    val m = state.remainingSeconds / 60
                    val s = state.remainingSeconds % 60
                    binding.tvTimerVal.text =
                        String.format(Locale.getDefault(), "%02d:%02d", m, s)

                    // ── Progress bars
                    val progressValue = (state.progress * 10_000).toInt()
                    binding.progressTimer.progress = progressValue
                    binding.progressDock.progress  = progressValue

                    // ── Sound labels (only when populated)
                    if (state.soundTitle.isNotEmpty()) {
                        binding.tvTrackTitle.text    = state.soundTitle
                        binding.tvTrackSubtitle.text = state.soundSubtitle
                    }

                    // ── Pause button icon
                    binding.btnPause.setImageResource(
                        if (state.isPaused) android.R.drawable.ic_media_play
                        else android.R.drawable.ic_media_pause
                    )

                    // ── Animation state (only on change)
                    if (prevIsPaused != state.isPaused) {
                        if (state.isPaused) {
                            binding.orbContainer.clearAnimation()
                            stopEqualizerAnimation()
                        } else {
                            startBreathingAnimation()
                            startEqualizerAnimation()
                        }
                        prevIsPaused = state.isPaused
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is SessionEvent.NavigateToFinSession -> {
                            startActivity(
                                Intent(this@SessionImmersiveActivity, FinSessionActivity::class.java)
                                    .putExtra("DURATION_MS", event.durationMs)
                            )
                            finish()
                            fadeTransition()
                        }
                        SessionEvent.NavigateToHome -> {
                            startActivity(
                                Intent(this@SessionImmersiveActivity, HomeActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )
                            fadeTransition()
                        }
                    }
                }
            }
        }
    }

    // ═══════════════════════════════════════
    // ANIMATIONS
    // ═══════════════════════════════════════

    private fun startBreathingAnimation() {
        val breathe = ScaleAnimation(
            1.0f, 1.06f, 1.0f, 1.06f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration    = 4000
            repeatMode  = Animation.REVERSE
            repeatCount = Animation.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        binding.orbContainer.startAnimation(breathe)
    }

    private fun startGlowPulse() {
        val ring = binding.viewOrbRingOuter
        val scaleX = ObjectAnimator.ofFloat(ring, "scaleX", 1f, 1.2f).apply {
            repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; duration = 2500
            interpolator = AccelerateDecelerateInterpolator()
        }
        val scaleY = ObjectAnimator.ofFloat(ring, "scaleY", 1f, 1.2f).apply {
            repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; duration = 2500
            interpolator = AccelerateDecelerateInterpolator()
        }
        val alpha = ObjectAnimator.ofFloat(ring, "alpha", 0.5f, 0.05f).apply {
            repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; duration = 2500
            interpolator = AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply { playTogether(scaleX, scaleY, alpha); start() }

        ObjectAnimator.ofFloat(binding.viewBgGlow, "alpha", 0.25f, 0.12f).apply {
            repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; duration = 3000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun startEqualizerAnimation() {
        equalizerAnimators.clear()
        listOf(
            binding.eqBar1 to 380L,
            binding.eqBar2 to 600L,
            binding.eqBar3 to 340L,
            binding.eqBar4 to 700L,
            binding.eqBar5 to 430L,
        ).forEach { (bar, dur) ->
            ObjectAnimator.ofFloat(bar, "scaleY", 1f, 3f).apply {
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                duration = dur
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }.also { equalizerAnimators.add(it) }
        }
    }

    private fun stopEqualizerAnimation() {
        equalizerAnimators.forEach { it.cancel() }
        equalizerAnimators.clear()
    }

    private fun rotateMotivationalText() {
        lifecycleScope.launch {
            var i = 0
            while (true) {
                delay(8_000L)
                if (!viewModel.uiState.value.isPaused) {
                    i = (i + 1) % motivationalPhrases.size
                    binding.tvMotivational.animate().alpha(0f).setDuration(500).withEndAction {
                        binding.tvMotivational.text = motivationalPhrases[i]
                        binding.tvMotivational.animate().alpha(1f).setDuration(500).start()
                    }.start()
                }
            }
        }
    }

    // ═══════════════════════════════════════
    // LIFECYCLE
    // ═══════════════════════════════════════

    override fun onPause() {
        super.onPause()
        viewModel.onActivityPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActivityResume()
    }

    override fun onDestroy() {
        stopEqualizerAnimation()
        super.onDestroy()
    }
}
