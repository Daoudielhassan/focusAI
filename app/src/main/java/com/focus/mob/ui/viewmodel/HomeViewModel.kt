package com.focus.mob.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focus.mob.data.SessionRecord
import com.focus.mob.data.repository.SessionRepository
import com.focus.mob.ml.FeatureExtractor
import com.focus.mob.ml.RuleBasedRecommendationEngine
import com.focus.mob.ui.main.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SessionRepository,
    private val engine: RuleBasedRecommendationEngine
) : ViewModel() {

    private val quotes = listOf(
        "\"La concentration est la racine de toute réussite.\"",
        "\"Un seul objectif à la fois. Puis un autre.\"",
        "\"Le flux commence quand on s'oublie soi-même.\"",
        "\"Chaque session compte, même les plus courtes.\"",
        "\"Soyez présent. Maintenant. Ici.\""
    )

    private var quoteIndex = 0

    private val _uiState = MutableStateFlow(HomeUiState(currentQuote = quotes[0]))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        startQuoteRotation()
    }

    // ─── Actions ──────────────────────────────────────────────────

    fun selectDuration(minutes: Int) {
        _uiState.update { it.copy(selectedDuration = minutes) }
    }

    fun refresh() {
        loadHomeData()
    }

    // ─── Data loading ─────────────────────────────────────────────

    private fun loadHomeData() {
        viewModelScope.launch {
            val startOfDay = getStartOfDayMillis()
            val sessions = repository.getAllSessions()
            val todayMinutes = repository.getTodayFocusTime(startOfDay)
            val todaySessions = sessions.count { it.timestamp >= startOfDay }
            val streak = computeStreak(sessions)

            val features = FeatureExtractor.extract(sessions, todayMinutes)
            val reco = engine.recommend(features)

            _uiState.update {
                it.copy(
                    todayFocusMinutes         = todayMinutes,
                    todaySessions             = todaySessions,
                    streakDays                = streak,
                    focusScore                = reco.focusScore,
                    recommendedDuration       = reco.recommendedDuration,
                    recommendedSoundCategory  = reco.recommendedSoundCategory,
                    aiTip                     = reco.message
                )
            }
        }
    }

    // ─── Quote rotation ───────────────────────────────────────────

    private fun startQuoteRotation() {
        viewModelScope.launch {
            while (true) {
                delay(6_000L)
                quoteIndex = (quoteIndex + 1) % quotes.size
                _uiState.update { it.copy(currentQuote = quotes[quoteIndex]) }
            }
        }
    }

    // ─── Computations ─────────────────────────────────────────────

    private fun computeStreak(sessions: List<SessionRecord>): Int {
        if (sessions.isEmpty()) return 0
        val daySet = sessions
            .map { truncateToDay(it.timestamp) }
            .toSortedSet(reverseOrder())
        var streak = 0
        var expected = truncateToDay(System.currentTimeMillis())
        for (day in daySet) {
            if (day == expected) {
                streak++
                expected -= 86_400_000L
            } else if (day < expected) {
                break
            }
        }
        return streak
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private fun getStartOfDayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun truncateToDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
