package com.focus.mob.ui.session
import com.focus.mob.R

import com.focus.mob.ui.auth.*
import com.focus.mob.ui.main.*
import com.focus.mob.ui.onboarding.*
import com.focus.mob.ui.session.*
import com.focus.mob.utils.*

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.focus.mob.databinding.ActivitySessionImmersiveBinding
import com.focus.mob.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class SessionImmersiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionImmersiveBinding
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis = 0L
    private var originalDurationMs = 0L
    private var isPaused = false

    // ─── ExoPlayer replaces MediaPlayer ──────────────────────────
    private var exoPlayer: ExoPlayer? = null

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

        originalDurationMs = intent.getLongExtra("DURATION_MS", 25 * 60 * 1000L)
        timeLeftInMillis = originalDurationMs
        Timber.d("Session started: duration=${originalDurationMs}ms")

        setupListeners()
        updateProgress()
        startBreathingAnimation()
        startGlowPulse()
        startEqualizerAnimation()
        startTimer()
        fetchAndPlayRadio()
        rotateMotivationalText()
    }

    // ═══════════════════════════════════════
    // LISTENERS
    // ═══════════════════════════════════════

    private fun setupListeners() {
        binding.btnPause.setOnClickListener {
            if (isPaused) {
                isPaused = false
                binding.btnPause.setImageResource(android.R.drawable.ic_media_pause)
                startTimer()
                startBreathingAnimation()
                startEqualizerAnimation()
                exoPlayer?.play()
                Timber.d("Session resumed")
            } else {
                isPaused = true
                binding.btnPause.setImageResource(android.R.drawable.ic_media_play)
                countDownTimer?.cancel()
                binding.orbContainer.clearAnimation()
                stopEqualizerAnimation()
                exoPlayer?.pause()
                Timber.d("Session paused")
            }
        }

        binding.btnEmergency.setOnClickListener {
            it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100).withEndAction {
                Timber.i("Session abandoned by user")
                countDownTimer?.cancel()
                releasePlayer()
                startActivity(Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                fadeTransition()
            }.start()
        }
    }

    // ═══════════════════════════════════════
    // TIMER
    // ═══════════════════════════════════════

    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 200) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateProgress()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateProgress()
                Timber.i("Session completed! duration=${originalDurationMs}ms")
                releasePlayer()
                startActivity(
                    Intent(this@SessionImmersiveActivity, FinSessionActivity::class.java).apply {
                        putExtra("DURATION_MS", originalDurationMs)
                    }
                )
                finish()
                fadeTransition()
            }
        }.start()
    }

    private fun updateProgress() {
        val minutes = (timeLeftInMillis / 1000).toInt() / 60
        val seconds = (timeLeftInMillis / 1000).toInt() % 60
        binding.tvTimerVal.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        val ratio = if (originalDurationMs > 0)
            (timeLeftInMillis.toFloat() / originalDurationMs.toFloat()) else 0f

        val progressValue = (ratio * 10000).toInt()
        binding.progressTimer.progress = progressValue
        binding.progressDock.progress = progressValue
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
            duration = 4000
            repeatMode = Animation.REVERSE
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

        val bgAlpha = ObjectAnimator.ofFloat(binding.viewBgGlow, "alpha", 0.25f, 0.12f).apply {
            repeatMode = ValueAnimator.REVERSE; repeatCount = ValueAnimator.INFINITE; duration = 3000
            interpolator = AccelerateDecelerateInterpolator()
        }
        bgAlpha.start()
    }

    private val equalizerAnimators = mutableListOf<ObjectAnimator>()

    private fun startEqualizerAnimation() {
        equalizerAnimators.clear()
        val bars = listOf(
            binding.eqBar1 to 380L,
            binding.eqBar2 to 600L,
            binding.eqBar3 to 340L,
            binding.eqBar4 to 700L,
            binding.eqBar5 to 430L,
        )
        bars.forEach { (bar, dur) ->
            val anim = ObjectAnimator.ofFloat(bar, "scaleY", 1f, 3f).apply {
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                duration = dur
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            equalizerAnimators.add(anim)
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
                delay(8000)
                if (!isPaused) {
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
    // RADIO — ExoPlayer + Retrofit
    // ═══════════════════════════════════════

    private fun fetchAndPlayRadio() {
        lifecycleScope.launch {
            try {
                // Retrofit call on IO dispatcher
                val stations = withContext(Dispatchers.IO) {
                    RetrofitClient.radioBrowserApi.searchStations(tag = "ambient", limit = 5)
                }

                if (stations.isNotEmpty()) {
                    val station = stations.first()
                    Timber.i("Radio station found: ${station.name} @ ${station.streamUrl}")
                    binding.tvTrackTitle.text = station.name.trim()
                    binding.tvTrackSubtitle.text = "${station.codec} · ${station.bitrate}kbps · En direct"
                    playWithExoPlayer(station.streamUrl)
                } else {
                    setOfflineFallback()
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch radio station")
                setOfflineFallback()
            }
        }
    }

    private fun playWithExoPlayer(streamUrl: String) {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> Timber.d("ExoPlayer: buffering...")
                        Player.STATE_READY -> {
                            Timber.d("ExoPlayer: ready, starting playback")
                            if (!isPaused) play()
                        }
                        Player.STATE_ENDED -> Timber.d("ExoPlayer: stream ended")
                        Player.STATE_IDLE -> Timber.d("ExoPlayer: idle")
                    }
                }
            })
        }
    }

    private fun setOfflineFallback() {
        binding.tvTrackTitle.text = "Playlist Relax"
        binding.tvTrackSubtitle.text = "Hors-ligne"
        Timber.w("Radio fallback: offline mode")
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    // ═══════════════════════════════════════
    // LIFECYCLE
    // ═══════════════════════════════════════

    override fun onPause() {
        super.onPause()
        if (!isPaused) exoPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!isPaused) exoPlayer?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        stopEqualizerAnimation()
        releasePlayer()
    }
}
