package com.focus.mob.ui.session

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focus.mob.music.MusicProvider
import com.focus.mob.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// ─── Navigation events (one-shot) ────────────────────────────────────────────
sealed class SessionEvent {
    data class NavigateToFinSession(val durationMs: Long) : SessionEvent()
    object NavigateToHome : SessionEvent()
}

@HiltViewModel
class SessionImmersiveViewModel @Inject constructor(
    private val musicProvider: MusicProvider,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val originalDurationMs: Long =
        savedStateHandle.get<Long>("DURATION_MS") ?: (30 * 60 * 1000L)

    private val plannedMinutes: Int = (originalDurationMs / 60_000L).toInt()

    // ─── Timestamp-based timer state ─────────────────────────────
    // elapsed = now - startedAt - totalPauseMillis
    // remaining = plannedDurationMs - elapsed
    private var startedAt        = 0L  // wall-clock ms when session began / last resumed
    private var pausedAt         = 0L  // wall-clock ms when user paused (0 = running)
    private var totalPauseMillis = 0L  // cumulative pause time

    // ─── UI State ─────────────────────────────────────────────────
    private val _uiState = MutableStateFlow(
        SessionUiState(
            plannedDurationMinutes = plannedMinutes,
            remainingSeconds       = (originalDurationMs / 1_000L).toInt(),
            progress               = 1f
        )
    )
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    // ─── Events ───────────────────────────────────────────────────
    private val _events = MutableSharedFlow<SessionEvent>()
    val events: SharedFlow<SessionEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
        loadMusic()
        Timber.d("SessionImmersiveViewModel: planned=${plannedMinutes}min")
    }

    // ═══════════════════════════════════════
    // PUBLIC ACTIONS
    // ═══════════════════════════════════════

    fun togglePause() {
        if (_uiState.value.isPaused) {
            // ── Resume: absorb the time spent paused ──────────────
            if (pausedAt > 0L) {
                totalPauseMillis += System.currentTimeMillis() - pausedAt
                pausedAt = 0L
            }
            _uiState.update { it.copy(isPaused = false, isRunning = true) }
            musicProvider.play()
            Timber.d("Session resumed (totalPauseMs=$totalPauseMillis)")
        } else {
            // ── Pause: record the moment ──────────────────────────
            pausedAt = System.currentTimeMillis()
            _uiState.update {
                it.copy(isPaused = true, isRunning = false, pausedCount = it.pausedCount + 1)
            }
            musicProvider.pause()
            Timber.d("Session paused (count=${_uiState.value.pausedCount})")
        }
    }

    fun abandon() {
        timerJob?.cancel()
        musicProvider.release()
        Timber.i("Session abandoned")
        viewModelScope.launch { _events.emit(SessionEvent.NavigateToHome) }
    }

    /** Activity.onPause() — audio silenced, timer keeps running (timestamp-safe). */
    fun onActivityPause() {
        if (!_uiState.value.isPaused) musicProvider.pause()
    }

    /** Activity.onResume() — restore audio if user hasn't explicitly paused. */
    fun onActivityResume() {
        if (!_uiState.value.isPaused) musicProvider.play()
    }

    // ═══════════════════════════════════════
    // TIMER — timestamp-based
    // ═══════════════════════════════════════

    private fun startTimer() {
        startedAt = System.currentTimeMillis()
        totalPauseMillis = 0L
        pausedAt = 0L

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true) }

            while (true) {
                delay(200L)

                // Skip ticks while user-paused — wall clock stops for the session
                if (_uiState.value.isPaused) continue

                val remainingMs = computeRemainingMillis()
                val remainingSec = (remainingMs / 1_000L).toInt()
                val elapsedSec  = (originalDurationMs / 1_000L).toInt() - remainingSec
                val progress     = remainingMs.toFloat() / originalDurationMs.toFloat()

                _uiState.update {
                    it.copy(
                        remainingSeconds = remainingSec,
                        elapsedSeconds   = elapsedSec,
                        progress         = progress
                    )
                }

                if (remainingMs <= 0L) {
                    onSessionComplete()
                    break
                }
            }
        }
    }

    /**
     * Remaining milliseconds calculated from wall-clock timestamps.
     * Accurate even after process goes to background or CPU throttles.
     */
    private fun computeRemainingMillis(): Long {
        val elapsed = System.currentTimeMillis() - startedAt - totalPauseMillis
        return (originalDurationMs - elapsed).coerceAtLeast(0L)
    }

    private fun onSessionComplete() {
        _uiState.update { it.copy(isRunning = false, progress = 0f, remainingSeconds = 0) }
        musicProvider.release()
        NotificationHelper.sendSessionCompleteNotification(context, plannedMinutes)
        Timber.i("Session completed: ${plannedMinutes}min")
        viewModelScope.launch {
            _events.emit(SessionEvent.NavigateToFinSession(originalDurationMs))
        }
    }

    // ═══════════════════════════════════════
    // MUSIC
    // ═══════════════════════════════════════

    private fun loadMusic() {
        viewModelScope.launch {
            val sound = musicProvider.loadAndPlay("ambient")
            _uiState.update { it.copy(soundTitle = sound.title, soundSubtitle = sound.subtitle) }
        }
    }

    // ═══════════════════════════════════════
    // LIFECYCLE
    // ═══════════════════════════════════════

    override fun onCleared() {
        timerJob?.cancel()
        musicProvider.release()
        super.onCleared()
    }
}
