package com.focus.mob.ui.main

import com.focus.mob.domain.model.DailyFocusStat

data class StatsUiState(
    val isLoading: Boolean = false,
    val totalMinutes: Int = 0,
    val totalSessions: Int = 0,
    val completionRate: Float = 0f,
    val streakDays: Int = 0,
    val weeklyFocus: List<DailyFocusStat> = emptyList(),
    val bestFocusHour: Int? = null,
    val bestSoundCategory: String? = null
)
