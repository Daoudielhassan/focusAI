package com.focus.mob.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focus.mob.data.repository.SessionRepository
import com.focus.mob.domain.usecase.GetBestFocusTimeUseCase
import com.focus.mob.domain.usecase.GetBestSoundCategoryUseCase
import com.focus.mob.domain.usecase.GetCompletionRateUseCase
import com.focus.mob.domain.usecase.GetStreakUseCase
import com.focus.mob.domain.usecase.GetWeeklyFocusStatsUseCase
import com.focus.mob.ui.main.StatsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: SessionRepository,
    private val getWeeklyFocusStats:   GetWeeklyFocusStatsUseCase,
    private val getCompletionRate:     GetCompletionRateUseCase,
    private val getStreak:             GetStreakUseCase,
    private val getBestFocusTime:      GetBestFocusTimeUseCase,
    private val getBestSoundCategory:  GetBestSoundCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init { loadStats() }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Single DB fetch — all use cases are pure in-memory
                val sessions     = repository.getAllSessions()
                val totalMinutes = repository.getTotalFocusTime()

                _uiState.update {
                    it.copy(
                        isLoading         = false,
                        totalMinutes      = totalMinutes,
                        totalSessions     = sessions.size,
                        completionRate    = getCompletionRate(sessions),
                        streakDays        = getStreak(sessions),
                        weeklyFocus       = getWeeklyFocusStats(sessions),
                        bestFocusHour     = getBestFocusTime(sessions),
                        bestSoundCategory = getBestSoundCategory(sessions)
                    )
                }
                Timber.d("StatsViewModel loaded: ${sessions.size} sessions, ${totalMinutes}min total")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Timber.e(e, "StatsViewModel: failed to load stats")
            }
        }
    }
}
